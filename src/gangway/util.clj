(ns gangway.util
  (:require [immutant.messaging :as msg]
            [gangway.worker]
            [gangway.disembark]))

(defn start-queue! [system {n :name worker :worker}]
  (msg/start n)
  (msg/listen n (partial worker system)))

(defn attache->queues [attache]
  (let [n (name attache)]
    {:in {:name (str "queue.in." n)
          :worker (partial gangway.worker/do-work attache)}
     :out {:name (str "queue.out." n)
           :worker (partial gangway.disembark/disembark! attache)}}))

;; TODO: handle exceptions
(defn start-queues! [system]
  (let [attaches (get-in system [:flare :attaches])
        queues (reduce (fn start-queues!- [c v]
                         (assoc c v (attache->queues v)))
                       {} attaches)
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

(comment
  (gangway.enqueue/enqueue! "queue.out.genius" {:hello "there"})

  )
