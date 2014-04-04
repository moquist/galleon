(ns gangway.publish
  (:require [immutant.messaging :as msg]
            [gangway.util :as gw-util]))

(defn publish
  [qid message]
  (let [qn (get-in gw-util/queues [qid :name])]
    (msg/publish "queue.showevidence-in" message)))

(defn incoming [req]
  (spit "/tmp/req.txt" (str req))
  (let [qid (get-in req [:route-params :qid])]
    (spit "/tmp/pub.txt" (str "publishing to: " qid))
    (str (publish qid (str req)))))

