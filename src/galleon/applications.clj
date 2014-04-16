(ns galleon.applications
  (:require [helmsman]
            [helmsman.uri :as h-uri]
            [dossier.core :as dossier]
            [dossier.system]
            [gangway.util :as gw-util]
            [gangway.web]
            [navigator]
            [timber.core :as timber]))

(def system-applications
  [{:app-name "Timber"
    :helmsman-definition timber/helmsman-assets}
   {:app-name "Dossier"
    :init-fn! nil #_dossier.system/db-init ;; this is broken
    :start-fn! nil
    :stop-fn! nil
    :helmsman-context "dossier"
    :helmsman-definition dossier/helmsman-definition}
   {:app-name "Navigator"
    :init-fn! navigator/init!
    :start-fn! nil
    :stop-fn! nil}
   {:app-name "Gangway"
    :init-fn! nil
    :start-fn! gw-util/start-queues!
    :stop-fn! nil
    :helmsman-context "gangway"
    :helmsman-definition gangway.web/helmsman-definition}])

(defn make-app-context
  [app]
  (into [:context (:helmsman-context app)]
        (:helmsman-definition app)))

(defn front-page-handler
  [request]
  (timber/base-page
   {:page-name "Galleon"
    :asset-uri-path (h-uri/relative-uri-str request (h-nav/id-to-uri request :timber/assets))
    :user-name "Test User Name"
    :main-menu nil
    :user-menu nil
    :page-content "Hello world."}))

(def helmsman-definition
  (into
   [[:get "/" front-page-handler]]
    (map make-app-context (remove #(nil? (:helmsman-context %)) system-applications))))

(def system-handler
  (helmsman/compile-routes helmsman-definition))

