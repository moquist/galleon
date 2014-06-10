(ns gangway.web-test
  (:require [clojure.test :refer :all]
            [clj-http.client :as http]
            [immutant.messaging :as msg]
            [immutant.util]
            [clojure.data.json :as json]
            [galleon]
            [gangway.auth :as gw-auth]
            [gangway.util :as gw-util]
            [gangway.web :as gw-web]))

(def token (:token (gw-auth/add-queue-token! "Testing" 3 (:db-conn galleon/system))))

(def invalid-message
  (json/json-str
   {:header {:operation :assert
             :entity-id {:task-id 137}
             :entity-type :task}
    :payload {:entity {:id-sk-origin "vlacs"
                       :name "Master the art of addition"
                       :description "This is some sort of description"}}}))

(def valid-message
  (json/json-str
   {:header {:operation :assert
             :entity-id {:task-id 17}
             :entity-type :task}
    :payload {:entity {:id-sk-origin "vlacs"
                       :name "Master the art of addition"
                       :version "v3"
                       :description "This is some sort of description"}}}))

#_(deftest remote-http-test-malformed
  (let [result (http/post
                (format "%s/gangway/in/showevidence" (immutant.util/app-uri))
                {:headers {"Authorization" (format "Token %s" token)}
                 :body invalid-message
                 :throw-exceptions false})]
    (testing "malformed request (missing a required field)"
      (is (= 400 (:status result))))))

(deftest remote-http-test-success
  (let [result (http/post
                (format "%s/gangway/in/showevidence" (immutant.util/app-uri))
                {:headers {"Authorization" (format "Token %s" token)}
                 :body valid-message
                 :throw-exceptions false})]
    (testing "successful request"
      (is (= 201 (:status result))))))

(deftest remote-http-test-authentication-incorrect-token
  (let [result (http/post
                (format "%s/gangway/in/showevidence" (immutant.util/app-uri))
                {:headers {"Authorization" "Token thisisnotavalidtoken"}
                 :body valid-message
                 :throw-exceptions false})]
    (testing "incorrect authentication token"
      (is (= 401 (:status result))))))

(deftest remote-http-test-authentication-no-header
  (let [result (http/post
                (format "%s/gangway/in/showevidence" (immutant.util/app-uri))
                {:body valid-message
                 :throw-exceptions false})]
    (testing "authentication token missing from header"
      (is (= 401 (:status result))))))
