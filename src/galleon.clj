(ns galleon
  (:require [immutant.web :as web]
            [clojure.tools.logging]
            [taoensso.timbre :as timbre]
            [helmsman]
            [galleon.applications]
            [galleon.cli]
            [datomic.api :as d]
            [clojure.edn]
            [gangway.worker :as gw-worker]
            [datomic-schematode :as dst]
            [gangway.enqueue :as gw-enqueue]
            [attache]
            )
  (:import (java.io File)))

(def system nil)

(def default-logging-options
 {
  ;; Prefer `level-atom` to in-config level when possible:
  ;; :current-logging-level :debug

  ;;; Control log filtering by namespace patterns (e.g. ["my-app.*"]).
  ;;; Useful for turning off logging in noisy libraries, etc.
  :ns-whitelist []
  :ns-blacklist []

  ;; Fns (applied right-to-left) to transform/filter appender fn args.
  ;; Useful for obfuscating credentials, pattern filtering, etc.
  :middleware []

  ;;; Control :timestamp format
  :timestamp-pattern "yyyy-MMM-dd HH:mm:ss ZZ" ; SimpleDateFormat pattern
  :timestamp-locale  nil ; A Locale object, or nil

  ;; Output formatter used by built-in appenders. Custom appenders may (but are
  ;; not required to use) its output (:output). Extra per-appender opts can be
  ;; supplied as an optional second (map) arg.
  :fmt-output-fn (fn [{:keys [message]}]
                   message)

  :shared-appender-config {} ; Provided to all appenders via :ap-config key
  :appenders
  {:standard-out
   {:doc "Prints to *out*/*err*. Enabled by default."
    :min-level nil :enabled? true :async? false :rate-limit nil
    :fn (fn [{:keys [level ns output]}] ; Can use any appender args
          (clojure.tools.logging/log ns level nil output)
          )}

   :spit
   {:doc "Spits to `(:spit-filename :shared-appender-config)` file."
    :min-level nil :enabled? false :async? false :rate-limit nil
    :fn (fn [{:keys [ap-config output]}] ; Can use any appender args
          (when-let [filename (:spit-filename ap-config)]
            (try (spit filename (str output "\n") :append true)
                 (catch java.io.IOException _))))}}})
 

(defn file-exists? [path]
  (if (.isFile (File. path)) true false))

(defn load-system-config []
  (let [path "galleon-conf.edn"]
    (if (file-exists? path)
      (assoc (clojure.edn/read-string (slurp path))
             :enqueue-fn gw-enqueue/enqueue!
             )
      (throw (Exception. (str "Config file missing: " path))))))

(defn init-schema!
  "Combines schematode schema and transacts it in."
  [db-conn applications]
  (dst/init-schematode-constraints! db-conn)
  (dst/load-schema! db-conn
                           (reduce (fn combine-schemas- [schema app] (concat schema (:schema app)))
                                   []
                                   applications)))

(defn init-system!
  "Creates the system state from the config and applications maps."
  [config-map applications]
  (let [datomic-uri (:datomic-url config-map)
        db-create-rval (d/create-database datomic-uri)
        db-conn (d/connect datomic-uri)
        system {:db-conn db-conn
                :config config-map
                :attaches {:endpoints attache/endpoints
                           :transforms attache/transformations}}]
    (init-schema! db-conn applications)
    (doseq [app applications]
      (when (fn? (:init-fn! app))
        ((:init-fn! app) system)))
    system))

;;; TODO: Give reduce a try instead of (l)oop-recur.
(defn start-system!
  [_] ;; TODO: handle incoming system here?
  (timbre/merge-config! default-logging-options)
  (timbre/debug "Galleon starting up...")
  (let [apps galleon.applications/system-applications
        system (init-system!
                (load-system-config)
                 apps)
        system (loop [system-startup-state system
                      app (first apps)
                      remaining-apps (vec (rest apps))]
                 (if (nil? app)
                   system-startup-state
                   (recur
                    (if-let [start-fn! (:start-fn! app nil)]
                      (do
                        (timbre/debug "Starting sub-system: " (:app-name app))
                        (start-fn! system-startup-state))
                      system-startup-state)
                    (first remaining-apps)
                    (vec (rest remaining-apps)))))]
    (timbre/debug "Starting immutant web server...")
    (assoc-in system [:web-server :immutant]
              (web/start (galleon.applications/system-handler system)))))

(defn stop-system!
  [system]
  (web/stop)
  (let [apps galleon.applications/system-applications]
    (loop [system-shutdown-state system
           app (last apps)
           remaining-apps (vec (butlast apps))]
      (if (nil? app)
        system-shutdown-state
        (recur
          (if-let [stop-fn! (:stop-fn! app)]
            (stop-fn! system-shutdown-state)
            system-shutdown-state)
          (last remaining-apps)
          (vec (butlast remaining-apps)))))))

(defn init
  []
  (alter-var-root #'system start-system!))

