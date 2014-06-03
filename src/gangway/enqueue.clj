(ns gangway.enqueue
  (:require [immutant.messaging :as msg]
            [gangway.util :as gw-util]))

(defn enqueue! [qid message]
  (let [qn (get-in gw-util/queues [qid :name])]
    (msg/publish qn message)))

