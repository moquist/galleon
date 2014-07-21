(ns gangway.transformations.show-evidence.user)

(def transform-key-map
  {:user/id-sk :extUserID
   :user/firstname :firstName
   :user/lastname :lastName
   :user/email :email
   :user/privilege :role})

(defn transform-role
  [privilege]
  (condp
    = privilege
    "ADMIN" "District Administrator"
    "STUDENT" "Student"
    "TEACHER" "Teacher"))

(def transform-value-map
  {:role transform-role})
 
