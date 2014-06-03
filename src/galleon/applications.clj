(ns galleon.applications
  (:require [helmsman]
            [helmsman.uri :as h-uri]
            [helmsman.navigation :as h-nav]
            [gangway.util]
            [gangway.schema]
            [gangway.web]
            [navigator.schema :as n-schema]
            [navigator]
            [timber.core :as timber]
            [traveler.core :as tr-core]
            [traveler.schema :as tr-schema]
            [flare.util]
            [flare.schema]
            [flare.web]))

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
    :helmsman-definition tr-core/helmsman-definition}
   {:app-name "Navigator"
    :schema n-schema/schema
    :helmsman-context "navigator"
    :helmsman-definition navigator/helmsman-def}
   {:app-name "Flare"
    :start-fn! flare.util/get-queues
    :schema flare.schema/schema
    :helmsman-context "flare"
    :helmsman-definition flare.web/helmsman-definition}
   {:app-name "Gangway"
    :start-fn! gangway.util/start-queues!
    :schema gangway.schema/gangway-schema
    :helmsman-context "gangway"
    :helmsman-definition gangway.web/helmsman-definition}
   #_
   {:app-name "Flare: Notifier"
    :start-fn! nil}])

(defn make-app-context
  [system app]
  (let [hd (:helmsman-definition app)]
    (into [:context (:helmsman-context app "/")]
          (hd system))))

(defn front-page-handler
  [request]
  (timber/base-page
   {:page-name "Galleon"
    :asset-uri-path (h-uri/relative-uri request (h-nav/id->uri-path request :timber/assets))
    :user-name "Test User Name"
    :main-menu nil
    :user-menu nil
    :page-content "Hello world."}))

(defn helmsman-definition [system]
  (into
   [[:get "/" front-page-handler]]
   (map (partial make-app-context system) system-applications)))

(defn system-handler [system]
  (helmsman/compile-routes (helmsman-definition system)))

