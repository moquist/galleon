(ns ^{:doc
      "Gangway is for queueing and (minimal, queue-wrapping) REST
       endpoints for data entering or leaving Galleon. Nothing from
       outside enters or leaves Galleon except by the Gangway."}
  gangway.web
  (:require [clojure.pprint :refer [pprint]]
            [ring.middleware.params :refer [wrap-params]]
            [liberator.core :refer [resource]]
            [liberator.dev :refer [wrap-trace]]
            [gangway.enqueue :as gw-enqueue]
            [gangway.validation :as gw-validation]
            [gangway.auth :as auth]))

(defn liberator-resources
  [db-conn]
  {:incoming! (resource :allowed-methods [:post]
                        :available-media-types ["text/plain"]
                        :authorized? (fn [ctx]
                                       (auth/validate-token ctx db-conn))
                        :handle-unauthorized "You are not authorized to access this resource."
                        :malformed? (fn  [ctx]
                                      (let [message (slurp (get-in ctx [:request :body]))]
                                        (not (gw-validation/valid-batch? message))))
                        :handle-malformed (fn [ctx]  (do
                                                       (prn "----- Malformed Gangway Message -----")
                                                       (pprint (slurp (get-in ctx [:request :body])))
                                                       (prn "--- End Malformed Gangway Message ---")))
                        :post! (fn incoming!- [ctx]
                                 (let [rp (get-in ctx [:request :route-params])
                                       qid (keyword (:qid rp))]
                                   (gw-enqueue/enqueue! qid (slurp (get-in ctx [:request :body]))))))

   :hi-there  (resource :available-media-types ["text/plain"]
                        :handle-ok (fn [_] "hi there"))})

(defn helmsman-definition
  [db-conn]
  [[:post "/in/:qid" (:incoming! (liberator-resources db-conn))]
   [:get "/out" (:hi-there (liberator-resources db-conn))]
   [wrap-params]
   [wrap-trace :header :ui]])
