(ns gangway.validation
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]
            [hatch]
            [schema.core :as s]
            [oarlock.validation :as oar-val]
            [traveler.validation :as tr-val]))

(defn construct-data
  "Combines route-params with incoming json message"
  [msg rp]
  (pprint msg)
  (merge msg {:id-sk (:id-sk rp)
              :id-sk-origin (keyword (:id-sk-origin rp))}))

(defn validator
  [validation-map entity-type data]
  (let [validation (entity-type validation-map)]
    (if validation
      (try
        (s/validate
         validation
         data)
        {:valid true :data data}
        (catch Exception e {:valid false :error (.getMessage e)}))
      {:valid true :data data})))

(def validation-maps
  {:task               oar-val/validations
   :perf-asmt          oar-val/validations
   :section            oar-val/validations
   :student2perf-asmt  oar-val/validations
   :user               tr-val/validations})

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
  [msg rp]
  (let [entity-type (keyword (:entity-type rp))
        validation-map (entity-type validation-maps)]
    (if (valid-json? msg)
      (let [parsed-msg (json/read-str msg :key-fn keyword)
            data (construct-data parsed-msg rp)]
        (if (nil? validation-map)
          true
          (validator validation-map entity-type data))))))

(comment

  (def msg {:name "flarp" :version "v2" :description "this is a description"})
  (def rp {:id-sk "392" :id-sk-origin "showevidence" :entity-type "task" :qid "showevidence"})

  )
