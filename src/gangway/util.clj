(ns gangway.util
  (:require [gangway.worker :as gw-worker]))

(def queues
  {:showevidence {:name "queue.showevidence-in"
                  :worker gw-worker/do-work}})

