(ns immutant.init
  (:require [galleon]))

(alter-var-root #'galleon/system galleon/start-system!)

