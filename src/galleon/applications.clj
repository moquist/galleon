(ns galleon.applications
  (:require [helmsman]
            [helmsman.uri :as h-uri]
            [helmsman.navigation :as h-nav]
            [dossier.core :as dossier]
            [dossier.system]
            [gangway.util :as gw-util]
            [gangway.web]
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
   {:app-name "Dossier"
    :init-fn! nil #_dossier.system/db-init ;; this is broken
    :helmsman-context "dossier"
    :helmsman-definition dossier/helmsman-definition}
   {:app-name "Navigator"
    :init-fn! navigator/init!}
   {:app-name "Gangway"
    :start-fn! gw-util/start-queues!
    :helmsman-context "gangway"
    :helmsman-definition gangway.web/helmsman-definition}])

(defn make-app-context
  [app]
  (into [:context (:helmsman-context app "/")]
        (:helmsman-definition app)))

(defn front-page-handler
  [request]
  (timber/base-page
   {:page-name "Galleon"
    :asset-uri-path (h-uri/relative-uri request (h-nav/id->path request :timber/assets))
    :user-name "Test User Name"
    :main-menu nil
    :user-menu nil
    :page-content "Hello world."}))

(def helmsman-definition
  (into
   [[:get "/" front-page-handler]]
    (map make-app-context system-applications)))

(def system-handler
  (helmsman/compile-routes helmsman-definition))

