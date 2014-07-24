(ns gangway.transformations.show-evidence
  (:require [flare.api.out]))

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
   (make-creation-opts flare.api.out/default-opts
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
