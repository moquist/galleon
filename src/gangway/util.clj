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
;; TODO: handle exceptions
  ([system] (start-queues! system queues))
  ([system queues]
     (dorun
      (map (fn start-queues!- [[k q]]
             (msg/start (:name q))
             (if (:worker-fn q) 
               (msg/listen (:name q) (:worker-fn q))))
           queues))
     (assoc-in system [:gangway :queues]
               (set (concat (get-in system [:gangway :queues])
                            (keys queues))))))

(defn stop-queues!
;; TODO: handle exceptions
  ([system] (stop-queues! system queues))
  ([system queues]
     (dorun
      (map (fn stop-queues!- [k]
             (let [q (queues k)]
               (msg/stop (:name q))))
           (get-in system [:gangway :queues])))
     (assoc-in system [:gangway :queues] #{})))
