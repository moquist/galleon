(ns gangway.util
  (:require [immutant.messaging :as msg]
            [gangway.worker]
            [gangway.disembark]
            [gangway.enqueue]))

(defn start-queue! [system {n :name worker :worker}]
  (msg/start n)
  (msg/listen n (partial worker system)))

(defn attache->queues [attache]
  (let [n (name attache)]
    {:in {:name (str "queue.in." n)
          :worker (partial gangway.worker/do-work attache)}
     :out {:name (str "queue.out." n)
           :worker (partial gangway.disembark/disembark! attache)}}))

;; example of structure in system
#_
{:showevidence {:in {:name "queue.in.showevidence"
                     :worker gangway.worker/do-work}
                :out {:name "queue.out.showevidence"
                      :worker gangway.disembark/disembark!}}
 :moodle {:in {:name "queue.in.moodle"
               :worker gangway.worker/do-work}
          :out {:name "queue.out.moodle"
                :worker gangway.disembark/disembark!}}}

;; TODO: handle exceptions
(defn start-queues! [system]
  (let [attaches (get-in system [:attaches :endpoints])
        queues (reduce (fn start-queues!- [c v]
                         (assoc c v (attache->queues v)))
                       {} attaches)
        qs (flatten (for [qsys queues] (vals (second qsys))))]
    (doseq [q qs] (start-queue! system q))
    (-> system
        (assoc-in [:gangway :queues] queues)
        (assoc-in [:attaches :outgoing-fns]
                  (into {}
                        (map (fn [i]
                               (let [n (get-in (second i) [:in :name])]
                                 [(first i)
                                  (partial gangway.enqueue/enqueue! n)]))
                             queues))))))

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
