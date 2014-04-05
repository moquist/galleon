(ns gangway.publish
  (:require [immutant.messaging :as msg]
            [liberator.core :refer [defresource]]
            [gangway.util :as gw-util]))

(defn publish! [qid message]
  (let [qn (get-in gw-util/queues [qid :name])]
    (msg/publish qn (str message))))

(defresource incoming!
  :allowed-methods [:put]
  :available-media-types ["text/plain"]
  :put! (fn incoming!- [req]
          (let [qid (keyword (get-in req [:request :route-params :qid]))]
            (publish! qid (str req) #_(get-in req [:request :body])))))

