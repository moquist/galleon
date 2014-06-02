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
                  :worker-fn gw-worker/do-work}
   :genius {:name "queue.genius-in"
            :worker-fn gw-worker/do-work}})

(defn start-queue! [system [k q]]
  (let [n (:name q)
        worker-fn (:worker-fn q)]
    (msg/start n)
    (when worker-fn
      (msg/listen n (partial worker-fn (:db-conn system))))))


;; TODO: handle exceptions
(defn start-queues!
  [system]
  (let [queues (:queues system)]
    (dorun (map (partial start-queue! system) queues))
    (let [path [:gangway :queues]]
      (assoc-in system path
                (set (concat (get-in system path)
                             (keys queues)))))))

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
