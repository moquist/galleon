(ns gangway.auth
  (:require [clojure.string :refer [split]]
            [clojure.pprint :refer [pprint]]
            [clj-time.core :as t]
            [clj-time.coerce :as t-c]
            [datomic.api :as d]
            [gangway.schema :as schema]
            [hatch]))

(defn expired?
  "Checks expiration time of token against current time

  Params:  q-res - result of d/q
           db    - Datomic database value
  Returns: boolean"
  [q-res db]
  (let [ent (d/entity db (ffirst q-res))
        expires (:queue-auth/expires ent)]
    (if (nil? expires)
      false
      (if (t/after? (t/now) (t-c/from-date expires))
        true
        false))))

(defn request->auth-token
  "Retrieves the authentication token from an incoming
  request.

  Params:  ctx - Liberator Context
  Returns: string"
  [ctx]
  (let [auth (get-in ctx [:request :headers "authorization"])]
    (if (nil? auth)
      (str "")
      (let [split-auth (split auth #"\s+")]
        (if (= (str "Token") (first split-auth))
          (second split-auth)
          (str ""))))))

(defn gen-token
  "Generates a random token of a specified length consisting
  of numbers, letters, and symbols.

  Params:  length - int
  Returns: string"
  [length]
  (apply str
         (take length
               (repeatedly
                #(rand-nth "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_")))))

(defn add-queue-token!
  "Add a token to use the queues, takes an owner as a string
   and an exipration time expressed in the number of months from
   the current time as an int"
  [owner expire db]
  (let [expiration (.toDate (t/plus (t/now) (t/months expire)))
        token      (gen-token 64)
        queue-auth {:token token
                    :owner owner
                    :expires expiration}]
    (schema/tx-entity! db :queue-auth (hatch/slam-all queue-auth :queue-auth))
    queue-auth))

(defn validate-token
  "Validates a token from an incoming request,
  designed to be used in the authorized? section
  of a liberator resource.

  Params:  ctx - Liberator Context
  db-conn  - Datomic database connection
  Returns: boolean"
  [ctx db-conn]
  (let [token  (request->auth-token ctx)]
    (if (empty? token)
      false
      (let [db (d/db db-conn)
            result (d/q '[:find ?e
                          :in $ ?token
                          :where [?e :queue-auth/token ?token]]
                        db
                        token)]
        (if (or (empty? result)
                (expired? result db))
          false
          true)))))
