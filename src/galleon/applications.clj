(ns galleon.applications
  (:require [helmsman]
            [helmsman.uri :as h-uri]
            [helmsman.navigation :as h-nav]
            [gangway.util :as gw-util]
            [gangway.schema :as gw-schema]
            [gangway.web]
            [navigator.schema :as n-schema]
            [navigator]
            [timber.core :as timber]
            [traveler.core :as tr-core]
            [traveler.schema :as tr-schema]))

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
   {:app-name "Traveler"
    :schema tr-schema/traveler-schema
    :helmsman-context "traveler"
    :helmsman-definition-db-conn true
    :helmsman-definition tr-core/helmsman-definition}
   {:app-name "Navigator"
    :schema n-schema/schema
    :helmsman-context "navigator"
    :helmsman-definition-db-conn true
    :helmsman-definition navigator/helmsman-def}
   {:app-name "Gangway"
    :start-fn! gw-util/start-queues!
    :schema gw-schema/gangway-schema
    :helmsman-context "gangway"
    :helmsman-definition-db-conn true
    :helmsman-definition gangway.web/helmsman-definition}
   #_
   {:app-name "Flare: Notifier"
    :start-fn! nil}])

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

