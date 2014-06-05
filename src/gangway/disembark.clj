(ns gangway.disembark)

(defn disembark! [attache system msg]
  (spit "/tmp/blarp.edn" (str msg) :append true))
