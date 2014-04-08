(ns ^{:doc
      "Gangway is for queueing and (minimal, queue-wrapping) REST
       endpoints for data entering or leaving Galleon. Nothing from
       outside enters or leaves Galleon except by the Gangway."}
  gangway.web
  (:require [ring.middleware.params :refer [wrap-params]]
            [liberator.dev :refer [wrap-trace]]
            [gangway.publish :as gw-publish]))

(def helmsman-definition
  [[:post "/in/:qid" gw-publish/incoming!]
   [wrap-params]
   [wrap-trace :header :ui]])
