(ns gangway.transformations
  (:require
    [hatch]
    [gangway.transformations.show-evidence :as se]
    [gangway.transformations.show-evidence.user :as se-user]))

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
    se-user/user-transform-key-map
    se-user/user-transform-value-map))
