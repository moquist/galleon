(ns gangway.auth
  (:require [clojure.string :refer [split]]
            [clojure.pprint :refer [pprint]]))

(defn request->auth-token
  "Retrieves the authentication token from an incoming
   request.

   Params:  ctx - Liberator Context
   Returns: string"
  [ctx]
  (let [auth (get-in ctx [:request :headers "authorization"])]
    (if (nil? auth)
      (str "")
      (second (split
           (get-in ctx [:request :headers "authorization"])
           #"\s+")))))

(defn gen-token
  "Generates a random token of a specified length consisting
   of numbers, letters, and symbols.

   Params:  length - int
   Returns: string"
  [length]
  (apply str
    (take length
      (repeatedly
        #(rand-nth "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-_")))))

(defn validate-token
  "Validates a token from an incoming request,
   designed to be used in the authorized? section
   of a liberator resource.

   TODO: Check if the incoming token exists in Datomic,
         don't hard code this.

   Params:  ctx - Liberator Context
   Returns: boolean"
  [ctx]
  (let [token (request->auth-token ctx)]
    (if (= token (str "T0_V&(1AZ#U1$X3EXQL@K!OJ568G4&3DL55!5MU16E#E6TY%KV!3O1QB&L2!QSXT"))
      true
      false)))
