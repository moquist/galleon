(ns gangway.worker
  (:require [clojure.data.json :as json]
            [gangway.validation :refer [construct-data]]
            [immutant.messaging :as msg]
            [navigator]))

(def worker-dispatch
  {:assert
   {:task           navigator/task-in
    :comp           navigator/comp-in
    :comp-tag       navigator/comp-tag-in
    :perf-asmt      navigator/perf-asmt-in
    :user2comp      navigator/user2comp-in
    :user2perf-asmt navigator/user2perf-asmt-in}})

(defn get-worker-fn [message]
  (let [header (:header message)
        op (keyword (:operation header))
        entity-type (keyword (:entity-type header))]
    (get-in worker-dispatch [op entity-type])))

(defn dispatch [db-conn message]
  (let [wf (get-worker-fn message)]
    ((get-worker-fn message) db-conn (construct-data message))))

(defn do-work [attache system messages]
  (let [db-conn (:db-conn system)]
    (let [parsed-messages (json/read-str messages :key-fn keyword)]
      (doall (map (partial dispatch db-conn) parsed-messages)))))

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
