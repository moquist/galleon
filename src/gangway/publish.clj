(ns gangway.publish
  (:require [immutant.messaging :as msg]
            [datomic.api :as d]
            [gangway.util :as gw-util]))

(defn publish! [qid message]
  (let [qn (get-in gw-util/queues [qid :name])]
    (msg/publish qn message)))

