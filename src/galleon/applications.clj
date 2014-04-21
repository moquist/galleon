(ns galleon.applications
  (:require [helmsman]
            [dossier.core :as dossier]
            [dossier.system]
            [gangway.util :as gw-util]
            [gangway.web]
            [navigator.schema :as n-schema]))

(def system-applications
  [{:app-name "Dossier"
    :init-fn! nil #_dossier.system/db-init ;; this is broken
    :start-fn! nil
    :stop-fn! nil
    :helmsman-context "dossier"
    :helmsman-definition dossier/helmsman-definition}
   {:app-name "Navigator"
    :schema n-schema/schema}
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

(def helmsman-definition
  (into
   [[:get "/" (constantly "Some page.")]]
    (map make-app-context (remove #(nil? (:helmsman-context %)) system-applications))))

(def system-handler
  (helmsman/compile-routes helmsman-definition))

