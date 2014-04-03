(ns gangway.web
  (:require [helmsman]
            [gangway.out :as gw-out]))

(def helmsman-definition
  [[:put "/in/:queue-name/:txid" gw-out/fake-work]])
