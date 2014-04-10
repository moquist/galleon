(ns galleon.applications
  (:require [helmsman]
            [dossier.core :as dossier]
            [dossier.system]
            [gangway.util :as gw-util]
            [gangway.web]))

(def system-applications
  [{:app-name "Dossier"
    :init-fn! dossier.system/db-init
    :start-fn! nil
    :stop-fn! nil
    :helmsman-context "dossier"
    :helmsman-definition dossier/helmsman-definition}
   {:app-name "Gangway"
    :init-fn! nil
    :start-fn! nil #_gw-util/start-queues!
    :stop-fn! nil
    :helmsman-context "gangway"
    :helmsman-definition gangway.web/helmsman-definition}])

(defn make-app-context
  [app]
  (prn app)
  (into [:context (:helmsman-context app)]
        (:helmsman-definition app)))

(def helmsman-definition
  (into
    [[:get "/" (constantly "Some page.")]]
    (map make-app-context system-applications)))

(def system-handler
  (helmsman/compile-routes helmsman-definition))

