(ns gangway.util
  (:require [immutant.messaging :as msg]
            [gangway.worker]
            [gangway.disembark]))

(defn start-queue! [system {n :name worker :worker}]
  (msg/start n)
  (msg/listen n (partial worker system)))

(defn queue-definitions [qid]
  (let [n (name qid)]
    {:in {:name (str "queue.in." n)
          :worker gangway.worker/do-work}
     :out {:name (str "queue.out." n)
           :worker gangway.disembark/disembark!}}))

;; TODO: handle exceptions
(defn start-queues! [system]
  (let [qids (get-in system [:flare :attaches])
        queues (reduce (fn start-queues!- [c v]
                         (assoc c v (queue-definitions v)))
                       {} qids)
        qs (flatten (for [qsys queues] (vals (second qsys))))]
    (dorun (map (partial start-queue! system) qs))
    (assoc-in system [:gangway :queues] queues)))

;; TODO: handle exceptions
(defn stop-queues! [system] 
  (let [queues (get-in system [:gangway :queues])]
    (dorun
     (map (fn stop-queues!- [k]
            (let [q (queues k)]
              (msg/stop (:name q))))
          (get-in system [:gangway :queues]))))
  (assoc-in system [:gangway :queues] {}))
