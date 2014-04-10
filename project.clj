(defproject galleon "0.1.0"
  :description "Galleon is a library that ties together multiple web app libraries
               to turn them into a single cohesive application."
  :url "https://www.github.com/vlacs/galleon"
  :license {:name "TODO: Choose a license"
            :url "http://choosealicense.com/"}
  :dependencies [^{:voom {:repo "https://github.com/vlacs/helmsman"}}
                 [org.vlacs/helmsman "0.1.9-20140407_022514-g5b65c22"]
                 [org.immutant/immutant-web "1.1.1"]
                 [org.immutant/immutant-messaging "1.1.1"
                  :exclusions [io.netty/netty]]
                 ^{:voom {:repo "https://github.com/vlacs/dossier"}}
                 [org.vlacs/dossier "0.1.4-1-20140402_072947-g4c9edc2"
                  :exclusions [hiccup org.clojure/tools.reader
                               compojure ring/ring-core commons-codec
                               com.datomic/datomic-free]]
                 ^{:voom {:repo "https://github.com/vlacs/navigator"}}
                 [org.vlacs/navigator "0.1.0-20140410_082313-g18713ff"
                  :exclusions [com.datomic/datomic-free]]
                 [http-kit "2.1.16"]
                 #_[ring "1.2.1"]
                 [liberator "0.11.0"]
                 [clj-http "0.9.1"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/clojure "1.6.0"]]

  :pedantic? :warn ; :abort
  :immutant {:context-path "/"}

  :profiles {:voom {:plugins [[lein-voom "0.1.0-SNAPSHOT" :exclusions [org.clojure/clojure]]]}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [com.datomic/datomic-free "0.9.4699"
                                   :exclusions [commons-codec]]]
                   :source-paths ["dev"]}
             :dev-pro {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                      [com.datomic/datomic-free "0.9.4699"
                                       :exclusions [commons-codec]]]
                       :source-paths ["dev"]}
             :production {:dependencies [[com.datomic/datomic-free "0.9.4699"
                                          :exclusions [commons-codec]]]}
             :production-pro {:repositories [["my.datomic.com" {:url "https://my.datomic.com/repo"
                                                                :username :env/lein_datomic_repo_username
                                                                :password :env/lein_datomic_repo_password}]]
                              :dependencies [[com.datomic/datomic-pro "0.9.4556"]]}})
