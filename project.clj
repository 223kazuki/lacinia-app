(defproject lacinia-app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 [duct/core "0.6.1"]
                 [com.walmartlabs/lacinia "0.23.0-rc-1"]
                 [com.walmartlabs/lacinia-pedestal "0.5.0"]]
  :plugins [[duct/lein-duct "0.10.5"]]
  :main ^:skip-aot lacinia-app.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.2.0"]
                                   [eftest "0.4.0"]
                                   [kerodon "0.9.0"]]}})
