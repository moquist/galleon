(ns gangway.publish
  (:require [immutant.messaging :as msg]
            [liberator.core :refer [defresource]]
            [datomic.api :as d]
            [gangway.util :as gw-util]))

(defn publish! [qid message]
  (let [qn (get-in gw-util/queues [qid :name])]
    (msg/publish qn (str message))))

(defresource incoming!
  :allowed-methods [:put]
  :available-media-types ["text/plain"]
  :put! (fn incoming!- [ctx]
          (let [rp (get-in ctx [:request :route-params])
                qid (keyword (:qid rp))
                txid (:txid rp)]
            (publish! qid (slurp (get-in ctx [:request :body]))))))

