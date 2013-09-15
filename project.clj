(defproject cljs-propaganda "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [propaganda "0.0.5-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "0.3.2"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild
  {:builds
   {:main {:source-paths ["src/cljs"],
           :compiler
           {:output-to "resources/js/example.js"
            :optimizations :advanced}}
    :dev {:source-paths ["src/cljs"],
          :compiler
          {:output-to "resources/js/example.js"
           :optimizations :whitespace}}}})
