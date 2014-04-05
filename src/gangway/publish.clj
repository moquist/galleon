(ns gangway.publish
  (:require [immutant.messaging :as msg]
            [gangway.util :as gw-util]))

(defn publish [qid message]
  (let [qn (get-in gw-util/queues [qid :name])]
    (msg/publish qn (str message))))

(defn incoming [req]
  (let [qid (keyword (get-in req [:route-params :qid]))]
    (str (publish qid (str req)))))

