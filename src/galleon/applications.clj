(ns galleon.applications
  (:require [helmsman]
            [dossier.core :as dossier]
            [dossier.system]))

(def system-applications
  {"dossier" {:init-fn dossier.system/db-init
              :start-fn nil
              :stop-fn nil
              :helmsman-context "dossier"
              :helmsman-definition dossier/helmsman-definition}})

(defn make-app-context
  [app]
  (prn app)
  (into [:context (:helmsman-context (second app))]
        (:helmsman-definition (second app))))

(def helmsman-definition
  (into
    [[:get "/" (constantly "Some page.")]]
    (map make-app-context system-applications)))

(prn helmsman-definition)

(def system-handler
  (helmsman/compile-routes helmsman-definition))

