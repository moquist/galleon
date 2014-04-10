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

(defn file-exists?
  [path]
  (prn path)
  (if
    (.isFile (File. path)) true false))

(defn load-system-config
  [path]
  (if (file-exists? path)
    (clojure.edn/read-string (slurp path))
    (throw (Exception. (str "Config file missing: " path)))))

(defn init-system-state!
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
  (let [system (init-system-state!
                {:datomic-url "datomic:mem://galleon-test"} ;; TODO: make this configurable
                galleon.applications/system-applications)]

    (gw-util/start-queues! gw-util/queues)

    (assoc-in system [:web-server :immutant]
              (web/start galleon.applications/system-handler))))

(defn stop-system!
  [system]
  nil
  )

(defn -main [& args]
  (alter-var-root #'*read-eval* (constantly false))
  (start-system!
    (let [opts (galleon.cli/get-opts args)]
      (start-system!
        (:config opts default-config-path))))
  1)

