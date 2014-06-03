(ns gangway.util
  (:require [immutant.messaging :as msg]
            [gangway.worker]
            [gangway.disembark]))

;; TODO: Figure out how to support incoming and outgoing queues with
;; the same key in a reasonable way.
;; Before this question can be answered, must figure out how outgoing
;; queues would be presented via REST.

#_
(def queues-v2
  [{:name "showevidence"
    :in-fn blah
    :out-fn blah}
   {:name "genius"
    :in-fn g-blah}])

#_
(def queues
  {:showevidence {:name "queue.showevidence-in"
                  ;; having a :worker-fn implies that galleon should start a listener
                  :worker-fn gangway.worker/do-work}
   :genius {:name "queue.genius-in"
            :worker-fn gangway.worker/do-work}})

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
  (let [qids (get-in system [:flare :queues])
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
