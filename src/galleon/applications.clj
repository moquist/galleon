(ns galleon.applications
  (:require [helmsman]
            [helmsman.uri :as h-uri]
            [helmsman.navigation :as h-nav]
            [gangway.util :as gw-util]
            [gangway.web]
            [navigator.schema :as n-schema]
            [navigator]
            [timber.core :as timber]))

(def system-applications
  [#_
   {:app-name "Poopdeck(tm)"
    :init-fn! nil
    :start-fn! nil
    :stop-fn! nil
    :helmsman-context "nil"
    :helmsman-definition nil}  
   {:app-name "Timber"
    :helmsman-definition timber/helmsman-assets}
   {:app-name "Navigator"
    :schema n-schema/schema
    :helmsman-definition-db-conn true
    :helmsman-definition navigator/helmsman-def}
   {:app-name "Gangway"
    :start-fn! gw-util/start-queues!
    :helmsman-context "gangway"
    :helmsman-definition gangway.web/helmsman-definition}])

(defn make-app-context
  [db-conn app]
  (let [hd (:helmsman-definition app)]
    (into [:context (:helmsman-context app "/")]
          (if (:helmsman-definition-db-conn app)
            (hd db-conn)
            hd))))

(defn front-page-handler
  [request]
  (timber/base-page
   {:page-name "Galleon"
    :asset-uri-path (h-uri/relative-uri request (h-nav/id->uri-path request :timber/assets))
    :user-name "Test User Name"
    :main-menu nil
    :user-menu nil
    :page-content "Hello world."}))

(defn helmsman-definition [db-conn]
  (into
   [[:get "/" front-page-handler]]
   (map (partial make-app-context db-conn) system-applications)))

(defn system-handler [db-conn]
  (helmsman/compile-routes (helmsman-definition db-conn)))

