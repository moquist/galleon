(ns galleon.applications
  (:require [helmsman]
            [helmsman.uri :as h-uri]
            [helmsman.navigation :as h-nav]
            [gangway.util]
            [gangway.schema]
            [gangway.web]
            [navigator.schema :as n-schema]
            [navigator]
            [oarlock.schema :as o-schema]
            [timber.core :as timber]
            [traveler]
            [traveler.schema :as tr-schema]
            [flare]))


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
   {:app-name "Flare"
    :schema flare/schema}
   {:app-name "Traveler"
    :init-fn! traveler/init!
    :start-fn! traveler/start!
    :schema tr-schema/schema}
   {:app-name "Navigator"
    :schema n-schema/schema
    :helmsman-context "navigator"
    :helmsman-definition navigator/helmsman-def}
   {:app-name "Oarlock"
    :schema o-schema/schema}
   {:app-name "Gangway"
    :start-fn! gangway.util/start-queues!
    :schema gangway.schema/gangway-schema
    :helmsman-context "gangway"
    :helmsman-definition gangway.web/helmsman-definition}])

(defn make-app-context
  [system app]
  (when-let [hd (:helmsman-definition app)]
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

