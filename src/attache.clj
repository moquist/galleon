(ns attache)

(def endpoints 
  {:moodle {:format :json
            :http-method :post}
   :moodle {:format :json :post}})

;;; Two-level hash map with the first level key being an endpoint and the
;;; second level key is the flare event that is being transformed. All values
;;; should be fns that take a single argument, a payload for the specified
;;; event.
(def outgoing-transformations
  {:moodle {:flare.event/flare.ping identity}
   :showevidence {:flare.event/flare.ping identity}})

;;; ??
(def incoming-transformations
  {})
