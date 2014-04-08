(ns gangway.publish
  (:require [immutant.messaging :as msg]
            [liberator.core :refer [defresource]]
            [datomic.api :as d]
            [gangway.util :as gw-util]))

(defn publish! [qid message]
  (let [qn (get-in gw-util/queues [qid :name])]
    (msg/publish qn (str message))))

(defresource incoming!
  :allowed-methods [:post]
  :available-media-types ["text/plain"]
  :post! (fn incoming!- [ctx]
          (let [rp (get-in ctx [:request :route-params])
                qid (keyword (:qid rp))]
            ;; TODO: assert in datomic to (1) have a complete queue log and (2) ensure idempotency
            (publish! qid (slurp (get-in ctx [:request :body]))))))

