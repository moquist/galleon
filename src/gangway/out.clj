(ns gangway.out
  (:require [immutant.messaging :as msg]))

(defn publish
  [message]
  (msg/publish "queue.gangway-in-showevidence" message))

(defn fake-work [req]
  (publish (str req))
  "yay")

