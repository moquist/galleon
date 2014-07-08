(ns gangway.enqueue
  (:require [immutant.messaging :as msg]))

(defn enqueue! [qn message]
  (msg/publish qn message))
