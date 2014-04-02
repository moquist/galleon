(defproject galleon "0.1.0"
  :description "Galleon is a library that ties together multiple web app libraries
               to turn them into a single cohesive application."
  :url "https://www.github.com/vlacs/galleon"
  :license {:name "TODO: Choose a license"
            :url "http://choosealicense.com/"}
  :dependencies [[vlacs/helmsman "0.1.4"]
                 [vlacs/dossier "0.1.4"]
                 [http-kit "2.1.16"]
                 [ring "1.2.1"]
                 [org.clojure/clojure "1.5.1"]]

  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [com.datomic/datomic-free "0.9.4699"]]
                   :source-paths ["dev"]}
             :dev-pro {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                      [com.datomic/datomic-free "0.9.4699"]]
                       :source-paths ["dev"]}
             :production {:repositories [["my.datomic.com" {:url "https://my.datomic.com/repo"
                                                            :username :env/lein_datomic_repo_username
                                                            :password :env/lein_datomic_repo_password}]]
                          :dependencies [[com.datomic/datomic-pro "0.9.4556"]]}})
