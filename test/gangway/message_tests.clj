(ns gangway.message-tests
  (:require [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop])) 

;; from test.check README.md
(defspec first-element-is-min-after-sorting ;; the name of the test
         100 ;; the number of iterations for test.check to test
         (prop/for-all [v (gen/such-that not-empty (gen/vector gen/int))]
           (= (apply min v)
              (first (sort v)))))
