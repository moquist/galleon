(ns attache)

(def endpoints 
  [:moodle :showevidence])

;;; Two-level hash map with the first level key being an endpoint and the
;;; second level key is the flare event that is being transformed. All values
;;; should be fns that take a single argument, a payload for the specified
;;; event.
(def default-transformation identity)
(def transformations
  {:moodle
   {:out
    {:flare.event/flare.ping identity}}
   :showevidence
   {:out
    {:flare.event/flare.ping identity}}})


