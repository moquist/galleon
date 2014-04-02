(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer (javadoc)]
   [clojure.pprint :refer (pprint)]
   [clojure.reflect :refer (reflect)]
   [clojure.repl :refer (apropos dir doc find-doc pst source)]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [galleon]))

(def system
  "A Var containing an object representing the application under
  development."
  nil)

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  ;; TODO: Load up configs here.
  (alter-var-root #'user/system
                  (constantly
                    {:web {:port 6543
                           :join? false}})))

(defn start
  "Starts the system running, updates the Var #'system."
  []
  ;; TODO: Start storage service and web server here.
  (alter-var-root #'user/system
                  assoc-in [:web :server] (galleon/start-jetty (:web system))))

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (when (not (nil? (get-in system [:web :server])))
    (galleon/stop-jetty (get-in system [:web :server]))
    (alter-var-root #'user/system assoc-in [:web :server] nil)))

(defn go
  "Initializes and starts the system running."
  []
  (init)
  (start)
  :ready)

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh :after 'user/go))
