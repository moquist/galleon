(ns galleon.cli
  (:require [clojure.tools.cli :as cli]))

(def cli-options
  [["-c" "--config" "Specify a config file to start Galleon with."]
   ["-h" "--help" "Print this help message" :flag true]])

(defn get-opts
  [args]
  (let [[opts args usage] (apply cli/cli args cli-options)]
    (when (:help opts)
      (println usage)
      (System/exit 0))
    opts))
