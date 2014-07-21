(ns gangway.transformations.show-evidence)

(def default-http-opts
  {:timeout 60
   :user-agent "org.vlacs/flare/http-kit"
   ;;;:method :post
   :headers {:Accept "application/json"
             :Accept-Charset "utf-8"
             :Cache-Control "no-cache"
             :Connection "keep-alive"
             :X-Requested-With "clojure/http-kit"}
   :keepalive 10000
   :insecure? false
   :follow-redirects false})

(defn make-url
  [base-url & segments]
  (apply str (interpose "/" (cons base-url segments))))

(defn generic-opts
  [defaults auth-token data]
  (-> defaults
      (assoc-in [:headers :Authorization] auth-token)
      (assoc :body data)))

(defn alter-creation-opts
  [opts url]
  (-> opts
      (assoc :method :post)
      (assoc :url url)))

(defn alter-update-opts
  ([opts url id-segments]
   (-> opts
       (assoc :method :put)
       (assoc :url (apply (partial make-url url) id-segments)))))

(defn make-creation-opts
  ([auth-token data url]
   (make-creation-opts default-http-opts
                       auth-token
                       data
                       url))
  ([defaults auth-token data url]
   (alter-update-opts (generic-opts
                        defaults
                        auth-token
                        data) url)))

(defn make-update-opts
  ([auth-token data url id-segments]
   (make-update-opts default-http-opts
                     auth-token
                     data
                     url
                     id-segments))
  ([defaults auth-token data url id-segments]
   (alter-update-opts (generic-opts defaults
                                    auth-token
                                    data)
                      url id-segments)))
