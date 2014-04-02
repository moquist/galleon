(ns galleon
  (:require [helmsman]
            [galleon.applications]
            [galleon.cli]
            [ring.adapter.jetty :as jetty]
            [datomic.api :as d]
            [clojure.edn])
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
  (let [datomic-uri (:datomic-connection-url config-map)
        db-create-rval (d/create-database datomic-uri)
        db-conn (d/connect datomic-uri)
        system {:db db-conn
                :config config-map}]
    (when db-create-rval
      (doseq [app applications]
        (when (fn? (:init-fn app))
          ((:init-fn app) system))))
    system))

(defn start-jetty
  "Starts a new jetty instance using our global app handler and provided
  arguments for the web server."
  [jetty-args]
  (jetty/run-jetty
    galleon.applications/system-handler
    jetty-args))

(defn stop-jetty
  "Stops a particular jetty instance that is currently running."
  [jetty-instance]
  (.stop jetty-instance)
  nil)

(defn start-system
  [config-path]
  (let [system (init-system-state!
                 (load-system-config config-path)
                 galleon.applications/system-applications)]
    (assoc-in system [:web-server :jetty]
              (start-jetty (get-in system [:config :web-server :jetty])))))

(defn stop-system
  [system]
  nil
  )

(defn -main [& args]
  (alter-var-root #'*read-eval* (constantly false))
  (start-system
    (let [opts (galleon.cli/get-opts args)]
      (start-system
        (:config opts default-config-path))))
  1)

