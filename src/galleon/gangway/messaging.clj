(ns galleon.gangway.messaging
  (:require [clojure.data.json :as json]
            [immutant.messaging :as msg]))

;; Just an example at this point:
#_(def entity-functions
  :user traveler/user-put
  :dossier dossier/dossier-put
  :task navigator/task-put)

;; TODO: Does do-work need to fire off worker threads, or has HornetQ already done that?
(defn do-work
  "Receives message from queue and does the work."
  [message]
  (let [parsed-message (json/read-str message :key-fn keyword)
        header (:header parsed-message)
        payload (:payload parsed-message)
        entity (keyword (:entity-type header))
        operation (keyword (:operation header))]
    ((operation entity-functions) entity (get-in header [:entity-id :user-id]) payload)))

(defn publish
  [queue-name id message]
  (msg/publish queue-name message))
