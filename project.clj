(defproject org.vlacs/galleon "0.1.0"
  :description "Galleon is a library that ties together multiple web app libraries
               to turn them into a single cohesive application."
  :url "https://www.github.com/vlacs/galleon"
  :license {:name "TODO: Choose a license"
            :url "http://choosealicense.com/"}
  :dependencies [^{:voom {:repo "https://github.com/vlacs/helmsman"}}
                 [org.vlacs/helmsman "0.2.5"]
                 [org.vlacs/traveler "0.2.10" :exclusions [org.vlacs/helmsman org.vlacs/hatch]]
                 #_[org.immutant/immutant "1.1.1"
                  :exclusions [org.clojure/core.memoize io.netty/netty org.hornetq/hornetq-core-client]]
                 ^{:voom {:repo "https://github.com/vlacs/timber"}}
                 [org.vlacs/timber "0.1.7"]
                 #_[org.immutant/immutant-messaging "1.1.1"
                  :exclusions [io.netty/netty]]
                 ^{:voom {:repo "https://github.com/vlacs/navigator" :branch "master"}}
                 [org.vlacs/navigator "0.1.0-SNAPSHOT"]
                 [http-kit "2.1.16"]
                 #_[ring "1.2.1"]
                 [liberator "0.11.0"]
                 [clj-http "0.9.1"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/clojure "1.6.0"]]

  :pedantic? :warn ; :abort
  :immutant {:context-path "/"}

  :plugins [[lein-immutant "1.2.1"]]

  :profiles {:voom {:plugins [[lein-voom "0.1.0-SNAPSHOT" :exclusions [org.clojure/clojure]]]}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [com.datomic/datomic-free "0.9.4707"
                                   :exclusions [commons-codec org.jgroups/jgroups org.jboss.logging/jboss-logging]]
                                  [org.clojure/test.check "0.5.7"]]
                   :source-paths ["dev"]}
             :dev-pro {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                      [com.datomic/datomic-free "0.9.4699"
                                       :exclusions [commons-codec org.jgroups/jgroups org.jboss.logging/jboss-logging]]]
                       :source-paths ["dev"]}
             :production {:dependencies [[com.datomic/datomic-free "0.9.4699"
                                          :exclusions [commons-codec org.jgroups/jgroups org.jboss.logging/jboss-logging]]]}
             :production-pro {:repositories [["my.datomic.com" {:url "https://my.datomic.com/repo"
                                                                :username :env/lein_datomic_repo_username
                                                                :password :env/lein_datomic_repo_password}]]
                              :dependencies [[com.datomic/datomic-pro "0.9.4556"]]}})
