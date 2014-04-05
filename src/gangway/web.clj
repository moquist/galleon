(ns gangway.web
  (:require [helmsman]
            [ring.middleware.params :refer [wrap-params]]
            [liberator.dev :refer [wrap-trace]]
            [gangway.publish :as gw-publish]))

(def helmsman-definition
  [[:put "/in/:qid/:txid" gw-publish/incoming!]
   [wrap-params]
   [wrap-trace :header :ui]])
