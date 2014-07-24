(ns gangway.disembark
  (:require 
    [flare.api.out]
    [flare.subscription]
    [flare.client]
    [gangway.transformations :as transform]
    [gangway.transformations.show-evidence :as t-se]
    [gangway.transformations.show-evidence.user :as t-se-user]
    [gangway.transformations.show-evidence.section :as t-se-section]
    ))

(defn get-destination-url
  [system client event-type]
  (when-let [sub (flare.subscription/get-subscription
                   (:db-conn system) client event-type)]
    (:subscription/url sub)))

(defn get-auth-token
  [system client]
  (when-let [ce (flare.client/get-client (:db-conn system) client)]
    (:client/auth-token ce)))

(defmulti api-call!
  (fn [system client event-type data]
    [client event-type]))

(defmethod api-call!
  [:show-evidence :event.type/traveler.user]
  [system client event-type data]
  (let [se-data (transform/transform-entity
                  data
                  t-se-user/transform-key-map
                  t-se-user/transform-value-map)]
    (flare.api.out/call-api)

    nil))

(defmethod api-call!
  [:show-evidence :event.type/oarlock.section]
  [system client event-type data]
  nil
  )

(defn disembark! 
  "This fn is a wrapper for the api-call! multimethod."
  [system client event-type data]
  (api-call! system client event-type data)
  #_(spit "/tmp/blarp.edn" (str msg) :append true))
