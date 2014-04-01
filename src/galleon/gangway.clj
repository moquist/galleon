(ns galleon.gangway
  (:require [helmsman :refer [compile-routes]]))

(defn fake-db-fn! [data]
  ;; Galleon doesn't actually have any integrated libs that would
  ;; update the DB, yet, so this fn stands in for fns that other libs
  ;; will provide.
  true)

(def helmsman-definition
  ;; TODO: relate :queue-name to a specific queue that gets started (right now in immutant/init.clj)
  [[:put "/gangway/:queue-name/:queue-entry-id" gangway-hdl]
                          ;;; And middleware.
   [wrap-trace :header :ui]
   [wrap-params]
   [wrap-multipart-params]
   [wrap-file-info]
   [wrap-host-urls] ;;; Helmsman is going to handle this.
   ])

(def app (compile-routes helmsman-definition))
