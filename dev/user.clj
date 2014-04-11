(ns user
  (:require [immutant.util :refer [in-immutant?] :as util]
            [immutant.dev :refer [reload-project!] :as dev]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [datomic.api :as d]
            [galleon]
            [navigator]))

(defn stop! []
  (alter-var-root #'galleon/system galleon/stop-system!))

(defn start! []
  (alter-var-root #'galleon/system galleon/start-system!))

(defn reset
  "If you are accustomed to tools.namespace and reset, you can use this.
   It makes you feel better."
  []
  (stop!)
  (reload-project!)
  (start!))

(defn reset-and-delete-db! [delete-db]
  (when (= :delete-db delete-db)
    (stop!)
    (d/delete-database (get-in galleon/system [:config :datomic-url]))
    (reload-project!)
    (start!)))

(defn touch-that
  "Execute the specified query on the current DB and return the
   results of touching each entity.

   The first binding must be to the entity.
   All other bindings are ignored."
  [query & data-sources]
  (map #(d/touch
         (d/entity
          (d/db (:db-conn galleon/system))
          (first %)))
       (apply d/q query (d/db (:db-conn galleon/system)) data-sources)))

(defn ptouch-that
  "Example: (ptouch-that '[:find ?e :where [?e :user/username]])"
  [query & data-sources]
  (pprint (apply touch-that query data-sources))) 

(comment
  (navigator/create-competency (:db-conn galleon/system) "comp1" "v1" :comp.status/active)
  (ptouch-that '[:find ?e :where [?e :comp/name]])
  )

