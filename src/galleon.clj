(ns galleon
  (:require [immutant.web :as web]
            [immutant.messaging :as msg]
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

(comment
(defn initialize!
  []
  (web/start "/" g-web/app)
  
  ;;;; TODO: Support adding new named queues.
  ;; start the incoming message queue
  (msg/start "queue.gangway-in-showevidence")
  (msg/listen "queue.gangway-in-showevidence" gw-messaging/do-work)

  ;; start the outgoing message queue
  (msg/start "queue.gangway-out-showevidence")
  (msg/listen "queue.gangway-out-showevidence" gw-messaging/do-work)

  ;; initialize db connection
  ;; TODO: init datomic here (memdb is fine for now, eventually need configured URI)
  #_(def db (edn/read-string (slurp (format "%s/.kilo/kilo-conf-sql-db.edn" (System/getProperty "user.home")))))
  #_(k-sqldb/default-connection! db))

  ;; in the context of immutant starting up, this function gets called
  (initialize!)

  )

(defn start-queues! [& queues]
  (spit "/tmp/db0.txt" (str "waka: 3 pizzas" queues))
  (dorun (map (fn start-queues!- [q]
                (let [q (gw-util/queues q)]
                  (spit "/tmp/db1.txt" (str "waka: starting queue" (:name q)))
                  (msg/start (:name q))
                  (if (:worker q) 
                    ;;(msg/listen (:name q) (:worker q))
                    ;;(msg/listen "queue.showevidence-in" (fn start-queues!-2 [msg] (spit "/tmp/dun.txt" (str msg))))
                    (msg/listen (:name q) (:worker q))
                    ;;(msg/listen "queue.showevidence-in" gw-worker/do-work)
                    )))
              queues)))

(defn start-system!
  []
  (let [system (init-system-state!
                {:datomic-url "datomic:mem://galleon-test"} ;; TODO: make this configurable
                galleon.applications/system-applications)]

    ;; Where is this state? Does it matter?
    (start-queues! :showevidence)


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

