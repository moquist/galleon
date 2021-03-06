(defproject org.vlacs/galleon "0.1.1-SNAPSHOT"
  :description "Galleon is a library that ties together multiple web app libraries
               to turn them into a single cohesive application."
  :url "https://www.github.com/vlacs/galleon"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.4"]

                 [org.clojure/tools.logging "0.3.0"]
                 [com.taoensso/timbre "3.2.1"]

                 ^{:voom {:repo "https://github.com/vlacs/helmsman"}}
                 [org.vlacs/helmsman "0.2.6-20140619_005947-gb4bb7d4" :exclusions [org.eclipse.jetty.orbit/javax.servlet com.taoensso/timbre]]
                 ^{:voom {:repo "https://github.com/vlacs/navigator" :branch "dev"}}
                 [org.vlacs/navigator "0.1.3-20140722_131145-g262a968" :exclusions [com.datomic/datomic-free org.vlacs/hatch]]
                 ^{:voom {:repo "https://github.com/vlacs/oarlock" :branch "dev"}}
                 [org.vlacs/oarlock "0.1.0-20140721_170833-g437eae1" :exclusions [com.datomic/datomic-free org.vlacs/hatch]]
                 ^{:voom {:repo "https://github.com/vlacs/timber"}}
                 [org.vlacs/timber "0.1.7-20140625_192643-gf77b7f9"]
                 ^{:voom {:repo "https://github.com/vlacs/traveler" :branch "dev"}}
                 [org.vlacs/traveler "0.3.0-20140721_134856-g001f72c"
                  :exclusions [org.vlacs/helmsman com.datomic/datomic-free org.vlacs/hatch]]
                 ^{:voom {:repo "https://github.com/vlacs/flare" :branch "dev"}}
                 [org.vlacs/flare "0.1.0-20140723_135328-g0a4e347" :exclusions [com.datomic/datomic-free org.vlacs/hatch]]
                 ^{:voom {:repo "https://github.com/vlacs/hatch" :branch "dev"}}
                 [org.vlacs/hatch "0.2.3-20140721_152704-g587007c"]

                 [clj-http "0.9.1"]
                 [clj-time "0.7.0"]
                 [liberator "0.11.0"]
                 [org.immutant/immutant "1.1.3"
                  :exclusions [org.hornetq/hornetq-core-client io.netty/netty]]]

  :pedantic? :warn ; :abort

  :immutant {:init galleon/init
             :resolve-dependencies true
             :context-path "/"
             :virtual-host ["127.0.0.1" "khan.office.vlacs.org"]
             :nrepl-port 0}

  :plugins [[lein-cloverage "1.0.2"]
            [lein-immutant "1.2.1"]]

  :profiles {:voom           {:plugins [[lein-voom "0.1.0-20140427_205301-g84cf30c"
                                         :exclusions [org.clojure/clojure]]]}
             :dev            {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                             [com.datomic/datomic-free "0.9.4766.11"]
                                             [org.clojure/test.check "0.5.7"]]
                              :source-paths ["dev"]}
             :dev-pro        {:repositories [["my.datomic.com" {:url "https://my.datomic.com/repo"
                                                                ;; N.B.: The env vars must be in ALL_CAPS or they WILL_NOT_WORK.
                                                                :username :env/lein_datomic_repo_username
                                                                :password :env/lein_datomic_repo_password}]]
                              :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                             [com.datomic/datomic-pro "0.9.4766"]
                                             [org.clojure/test.check "0.5.7"]]
                              :source-paths ["dev"]}
             :production     {:dependencies [[com.datomic/datomic-free "0.9.4766"]]}
             :production-pro {:repositories [["my.datomic.com" {:url "https://my.datomic.com/repo"
                                                                :username :env/lein_datomic_repo_username
                                                                :password :env/lein_datomic_repo_password}]]
                              :dependencies [[com.datomic/datomic-pro "0.9.4766"]]}})
