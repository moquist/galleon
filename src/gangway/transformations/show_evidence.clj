(ns gangway.transformations.show-evidence
  (:require [flare.util]
            ))

(def user-transform-key-map
  {:id-sk :extUserID
   :firstname :firstName
   :lastname :lastName
   :email :email
   :privilege :role})

(def user-transform-val-map
  {})

(defn all-key-transformations
  []
  {:event.type/traveler.user }
  )

(comment
  (def example-user
    {:user/id-sk "12345"
     :user/id-sk-origin :local
     :user/username "jdoane"
     :user/password "some md5 hash..."
     :user/privilege "ADMIN"
     :user/lastname "Doane"
     :user/firstname "Jon"
     :user/email "jdoane@vlacs.org"
     :user/istest false
     :user/can-masquerade true})


  )
