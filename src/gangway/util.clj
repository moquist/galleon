(ns gangway.util
  (:require [gangway.worker :as gw-worker]))

;; TODO: Figure out how to support incoming and outgoing queues with
;; the same key in a reasonable way.
;; Before this question can be answered, must figure out how outgoing
;; queues would be presented via REST.
(def queues
  {:showevidence {:name "queue.showevidence-in"
                  :worker-fn gw-worker/do-work}})

