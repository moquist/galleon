(ns gangway.transformations)

(def key-transformations
  {:show-evidence}
  )

(defmulti transformation
  (fn [client event-type data]
    [client event-type]))

(defmethod transformation :default [client event-type data] data)
(defmethod transformation
  [:show-evidence :event.type/traveler.user]
  [_ event-type data]

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

