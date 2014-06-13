(ns gangway.validation
  (:require [clojure.data.json :as json]
            [hatch]
            [navigator.validation :as nav-val]
            [oarlock.validation :as oar-val]))

(defn construct-data
  "Constructs a namespaced map out of an incoming json message"
  [msg]
  (let [entity-type (get-in msg [:header :entity-type])]
    (if (get-in msg [:header :entity-id])
      (let [id-key (keyword (str entity-type "-id"))
            id-sk-key (keyword (str entity-type "/id-sk"))
            id (str (get-in msg [:header :entity-id id-key]))]
        (merge {id-sk-key id}
               (hatch/slam-all (get-in msg [:payload :entity]) (keyword entity-type))))
      (hatch/slam-all (get-in msg [:payload :entity]) (keyword entity-type)))))

(def validation-dispatch
  {:assert
   {:task           (partial oar-val/validator :task)
    :perf-asmt      (partial oar-val/validator :perf-asmt)
    :user2perf-asmt (partial oar-val/validator :user2perf-asmt)
    :user2comp      (partial nav-val/validator :user2comp)}})

(defn valid-json?
  "Evaluates given message string to determine if it's valid JSON.
  Returns true or false."
  [msg]
  (let [valid-msg
        (try
          (let [parsed-msg (json/read-str msg :key-fn keyword)]
            true)
          (catch Exception e
            (not true)))]
    valid-msg))

(defn valid?
  "Runs a validation function to check if a message is valid.
  Returns true or false."
  [msg]
  (let [header (:header msg)
        op (keyword (:operation header))
        entity-type (keyword (:entity-type header))
        validation-fn (get-in validation-dispatch [op entity-type])]
    (if (nil? validation-fn)
      true
      (if (nil? (first (validation-fn (construct-data msg))))
        true
        false))))

(defn valid-batch?
  "Runs valid? on a batch of json messages"
  [messages]
  (if (not (valid-json? messages))
    false
    (let [parsed-messages (json/read-str messages :key-fn keyword)]
      (if (some false? (doall (map (partial valid?) parsed-messages)))
        false
        true))))
