(ns gangway.worker
  (:require [clojure.data.json :as json]
            [immutant.messaging :as msg]
            [navigator]))

(def worker-dispatch
  "Maps incoming 'operation' value with a function that implements
   analagous operation via Korma db functions."
  {:assert
   {:task navigator/task-in}})

(defn get-worker-fn [message]
  (let [header (:header message)
        op (keyword (:operation header))
        entity-type (keyword (:entity-type header))]
    (get-in worker-dispatch [op entity-type])))

(defn dispatch [db-conn message]
  (let [wf (get-worker-fn message)]
  ((get-worker-fn message) db-conn message)))

(defn do-work [db-conn messages]
  (let [parsed-messages (json/read-str messages :key-fn keyword)]
    (doall (map (partial dispatch db-conn) parsed-messages))))

(comment

  (def messages
    (json/read-str
     (json/json-str
      [{:header {:operation :assert
                 :entity-id {:task-id 17}
                 :entity-type :task}
        :payload {:entity {:name "tie shoes (together)"
                           :version "v3"
                           :competency-parents [1 2 3]}}}])
     :key-fn keyword))
  )

