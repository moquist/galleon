(ns gangway.transformations
  (:require
    [hatch]
    [gangway.transformations.show-evidence.user :as se-user]))

(defmulti transformation
  (fn [client event-type http-options data]
    [client event-type]))

(defmethod transformation :default [_ _ http-options data] [http-options data])
(defmethod transformation
  [:show-evidence :event.type/traveler.user]
  [_ _ http-options data]
  nil  
  )

(defn filter-keep-on-map
  [data tkey-map]
  (clojure.set/rename-keys
    (into
      {}
      (filter
        (fn [[k v]]
          (contains?
            (set (keys tkey-map))
            k))
        data))
     tkey-map))

(defn map-on-map
  "Takes the data map and looks up it's keys in the transformation map. If a
  function exists, it will be called with the value of that pair. It will
  return the same data plus any transformations defined in the tval-map."
  [data tval-map]
  (into
    {}
    (map (fn [[k v]] 
           (if-let
             [t-fn (get tval-map k)]
             [k (t-fn v)]
             [k v]))
         data)))

(defn transform-entity
  [entity tkey-map tval-map]
  (map-on-map
    (filter-keep-on-map entity tkey-map)
    tval-map))

(comment

  (def test-data
    (hatch/slam-all {:id-sk "123abc"
                     :username "fbar"
                     :password "nothing"
                     :privilege "ADMIN"
                     :lastname "Bar"
                     :firstname "Foo"
                     :email "fbar@fbar.fbar"
                     :istest false
                     :can-masquerade true}
                    :user))

  (transform-entity
    test-data
    gangway.transformations.show-evidence/user-transform-key-map
    gangway.transformations.show-evidence/user-transform-value-map)

  (filter-keep-on-map
    test-data
    gangway.transformations.show-evidence/user-transform-key-map)

  )
