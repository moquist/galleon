(ns galleon
  (:require [immutant.web :as web]
            [helmsman]
            [galleon.applications]
            [galleon.cli]
            [datomic.api :as d]
            [clojure.edn]
            [gangway.worker :as gw-worker]
            [datomic-schematode.core :as schematode]
            [gangway.enqueue :as gw-enqueue])
  (:import (java.io File)))

(def default-config-path "/etc/galleon.edn")
(def system nil)

(defn file-exists?
  [path]
  (prn path)
  (if
    (.isFile (File. path)) true false))

(defn load-system-config []
  (let [path "aspire-conf.edn"]
    (if (file-exists? path)
      (assoc (clojure.edn/read-string (slurp path))
        :enqueue-fn gw-enqueue/enqueue!)
      (throw (Exception. (str "Config file missing: " path))))))

(defn init-schema!
  "Combines schematode schema and transacts it in."
  [db-conn applications]
  (schematode/init-schematode-constraints! db-conn)
  (schematode/load-schema! db-conn
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
                :config config-map}]
    (init-schema! db-conn applications)
    (doseq [app applications]
      (when (fn? (:init-fn! app))
        ((:init-fn! app) system)))
    system))

;;; TODO: Give reduce a try instead of (l)oop-recur.
(defn start-system!
  [_] ;; TODO: handle incoming system here?
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
                      (start-fn! system-startup-state)
                      system-startup-state)
                    (first remaining-apps)
                    (vec (rest remaining-apps)))))]
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

;;; We can't use this anymore. Should we even keep it?
#_(defn -main [& args]
    (alter-var-root #'*read-eval* (constantly false))
    (start-system!
      (let [opts (galleon.cli/get-opts args)]
        (start-system!
          (:config opts default-config-path))))
    1)

