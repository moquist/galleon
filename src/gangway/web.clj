(ns gangway.web
  (:require [helmsman]
            [ring.middleware.params :refer [wrap-params]]
            [liberator.dev :refer [wrap-trace]]
            [gangway.publish :as gw-publish]))

(def helmsman-definition
  [[:post "/in/:qid" gw-publish/incoming!]
   [wrap-params]
   [wrap-trace :header :ui]])
