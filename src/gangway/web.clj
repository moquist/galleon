(ns ^{:doc
      "Gangway is for queueing and (minimal, queue-wrapping) REST
       endpoints for data entering or leaving Galleon. Nothing from
       outside enters or leaves Galleon except by the Gangway."}
  gangway.web
  (:require [ring.middleware.params :refer [wrap-params]]
            [liberator.core :refer [defresource]]
            [liberator.dev :refer [wrap-trace]]
            [gangway.publish :as gw-publish]))

(defresource incoming!
  :allowed-methods [:post]
  :available-media-types ["text/plain"]
  :post! (fn incoming!- [ctx]
          (let [rp (get-in ctx [:request :route-params])
                qid (keyword (:qid rp))]
            ;; TODO: assert in datomic to (1) have a complete queue log and (2) ensure idempotency
            (gw-publish/publish! qid (slurp (get-in ctx [:request :body]))))))

(defresource hi-there
  :available-media-types ["text/plain"]
  :handle-ok (fn [_] "hi there"))

(def helmsman-definition
  [[:post "/in/:qid" incoming!]
   [:get "/out" hi-there]
   [wrap-params]
   [wrap-trace :header :ui]])
