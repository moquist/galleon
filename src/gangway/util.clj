(ns gangway.util
  (:require [immutant.messaging :as msg]
            [gangway.worker :as gw-worker]))

;; TODO: Figure out how to support incoming and outgoing queues with
;; the same key in a reasonable way.
;; Before this question can be answered, must figure out how outgoing
;; queues would be presented via REST.
(def queues
  {:showevidence {:name "queue.showevidence-in"
                  ;; having a :worker-fn implies that galleon should start a listener
                  :worker-fn gw-worker/do-work}})

(defn start-queues!
  ([] (start-queues! queues))
  ([queues]
     (dorun
      (map (fn start-queues!- [[k q]]
             (msg/start (:name q))
             (if (:worker-fn q) 
               (msg/listen (:name q) (:worker-fn q))))
           queues))))


