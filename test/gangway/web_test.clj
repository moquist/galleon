(ns gangway.web-test
  (:require [clojure.test :refer :all]
            [clj-http.client :as http]
            [immutant.messaging :as msg]
            [immutant.util]
            [clojure.data.json :as json]
            [gangway.util :as gw-util]
            [gangway.web :as gw-web]))

(deftest remote-http-test
  (let [result (http/post
                (format "%s/gangway/in/showevidence" (immutant.util/app-uri))
                {:body (json/json-str {:a "b"})
                 :throw-exceptions false})]
    (testing "malformed handler"
      (is (= 400 (:status result))))))
