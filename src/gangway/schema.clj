(ns gangway.schema
  (:require hatch))

(def gangway-schema
  "Gangway datomic schema"
  [[:queue-auth {:attrs [[:token :string :db.unique/identity]
                         [:owner :string]
                         [:expires :instant]]}]])

(def partitions (hatch/schematode->partitions gangway-schema))

(def valid-attrs (hatch/schematode->attrs gangway-schema))

(def tx-entity! (partial hatch/tx-clean-entity! partitions valid-attrs))
