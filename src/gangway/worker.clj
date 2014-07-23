(ns gangway.worker
  (:require [clojure.data.json :as json]
            [immutant.messaging :as msg]
            [navigator]
            [traveler]
            [oarlock]
            [navigator]))

(def worker-dispatch
  {:task               oarlock/task-in
   :perf-asmt          oarlock/perf-asmt-in
   :section            oarlock/section-in
   :student2perf-asmt  oarlock/student2perf-asmt-in
   :user               traveler/user-in
   :comp               navigator/comp-in})

(defn do-work [attache system message]
  (let [db-conn (:db-conn system)]
    #_(clojure.pprint/pprint message)   ; if you're going to uncomment this, consider using timbre
    (((:entity-type message) worker-dispatch) db-conn message)))

(comment

  (def message-v2
    (json/read-str
     (json/write-str
      {:operation :assert
       :entity-type :task
       :entity {:id-sk-origin "Show Evidence"
                :name "tie shoes (together)"
                :version "v3"
                :description "this is a mighty fine description"}})))

  (def messages
    (json/read-str
     (json/json-str
      {:header {:operation :assert
                 :entity-id {:task-id 17}
                 :entity-type :task}
        :payload {:entity {:id-sk-origin "se"
                           :name "tie shoes (together)"
                           :version "v3"
                           :description "this is a description"}}})
     :key-fn keyword))
  )
