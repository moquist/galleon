(ns immutant.init
  (:require [clojure.edn :as edn]
            [galleon.gangway.messaging :as gw-messaging]
            [galleon.web :as g-web]
            [immutant.messaging :as msg]))

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
