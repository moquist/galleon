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

(defn liberator-resources [system]
  (let [db-conn (:db-conn system)]
    {:incoming! (resource :allowed-methods [:post]
                          :available-media-types ["text/plain" "application/json"]
                          :authorized? (fn [ctx]
                                         (auth/validate-token ctx db-conn))
                          :handle-unauthorized "You are not authorized to access this resource."
                          :malformed? (fn [ctx]
                                        (let [message (slurp (get-in ctx [:request :body]))
                                              route-params (get-in ctx [:request :route-params])
                                              validated (gw-validation/valid? message route-params :assert)]
                                          (if (:valid validated)
                                            [false  {::validated (:data  validated)}]
                                            [true {::malformed (:error validated)}])))
                          :handle-malformed (fn [ctx] (str (::malformed ctx)))
                          :post! (fn incoming!- [ctx]
                                   (let [rp  (get-in ctx [:request :route-params])
                                         qid (keyword (:qid rp))
                                         n   (get-in system [:gangway :queues qid :in :name])]
                                     (gw-enqueue/enqueue! n (::validated ctx)))))

     :hi-there  (resource :available-media-types ["text/plain"]
                          :handle-ok (fn [_] "hi there"))}))

(defn helmsman-definition
  [system]
  [[:post "/in/:qid/:id-sk-origin/:entity-type/:id-sk"  (:incoming! (liberator-resources system))]
   [:get "/out" (:hi-there (liberator-resources system))]
   [wrap-params]
   [wrap-trace :header :ui]])
