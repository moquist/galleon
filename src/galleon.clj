(ns galleon
  (:require [immutant.web :as web]
            [helmsman]
            [galleon.applications]
            [galleon.cli]
            [datomic.api :as d]
            [clojure.edn]
            [gangway.util :as gw-util]
            [gangway.worker :as gw-worker])
  (:import (java.io File)))

(def default-config-path "/etc/galleon.edn")
(def system nil)

(defn file-exists?
  [path]
  (prn path)
  (if
    (.isFile (File. path)) true false))

(defn load-system-config
  [path]
  {:datomic-url "datomic:mem://galleon-test"}
  #_(if (file-exists? path)
    (clojure.edn/read-string (slurp path))
    (throw (Exception. (str "Config file missing: " path)))))

(defn init-system!
  "Creates the system state from the config and applications maps."
  [config-map applications]
  (let [datomic-uri (:datomic-url config-map)
        db-create-rval (d/create-database datomic-uri)
        db-conn (d/connect datomic-uri)
        system {:db-conn db-conn
                :config config-map}]
    (when db-create-rval
      (doseq [app applications]
        (when (fn? (:init-fn app))
          ((:init-fn app) system))))
    system))

(defn start-system!
  []
  (let [apps galleon.applications/system-applications
        system (init-system!
                (load-system-config "to/some/path") ;; TODO: Make this configurable.
                 apps)]

    (gw-util/start-queues! gw-util/queues)

    ;;; Lets get some app-magic started, shall we?
    (loop [system-startup-state system
           app (first apps)
           remaining-apps (vec (rest apps))]
      (if (nil? app)
        system-startup-state
        (recur
          (if-let [start-fn! (:start-fn app nil)]
            (start-fn! system-startup-state)
            system-startup-state)
          (first remaining-apps)
          (vec (rest remaining-apps)))))

    (assoc-in system [:web-server :immutant]
              (web/start galleon.applications/system-handler))))

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

;;; We can't use this anymore. Should we even keep it?
#_(defn -main [& args]
    (alter-var-root #'*read-eval* (constantly false))
    (start-system!
      (let [opts (galleon.cli/get-opts args)]
        (start-system!
          (:config opts default-config-path))))
    1)

