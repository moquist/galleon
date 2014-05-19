(ns ^{:doc
      "Gangway is for queueing and (minimal, queue-wrapping) REST
       endpoints for data entering or leaving Galleon. Nothing from
       outside enters or leaves Galleon except by the Gangway."}
  gangway.web
  (:require [ring.middleware.params :refer [wrap-params]]
            [liberator.core :refer [defresource]]
            [liberator.dev :refer [wrap-trace]]
            [gangway.publish :as gw-publish]
            [gangway.validation :as gw-validation]))

(defresource incoming!
  :allowed-methods [:post]
  :available-media-types ["text/plain"]
  :malformed? (fn  [ctx]
                (let [message (slurp (get-in ctx [:request :body]))]
                  (gw-validation/valid? message)))
  :handle-malformed (fn [ctx]  (prn (str "Malformed Gangway Message: " (slurp (get-in ctx [:request :body])))))
  :post! (fn incoming!- [ctx]
          (let [rp (get-in ctx [:request :route-params])
                qid (keyword (:qid rp))]
            (gw-publish/publish! qid (slurp (get-in ctx [:request :body]))))))

(defresource hi-there
  :available-media-types ["text/plain"]
  :handle-ok (fn [_] "hi there"))

(def helmsman-definition
  [[:post "/in/:qid" incoming!]
   [:get "/out" hi-there]
   [wrap-params]
   [wrap-trace :header :ui]])
