(ns gangway.web
  (:require [helmsman]
            [gangway.publish :as gw-publish]))

(def helmsman-definition
  [[:put "/in/:qid/:txid" gw-publish/incoming!]])
