(defproject cljs-propaganda "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1859"]
                 [propaganda "0.1.2"]]
  :plugins [[lein-cljsbuild "0.3.3"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild
  {:builds
   [{:source-paths ["src/cljs"]
     :notify-command ["terminal-notifier" "-title" "lein-cljsbuild" "-message"]
     :compiler
     {:libs ["src/js"]
      :output-to "resources/js/example.js"
      :optimizations :whitespace
      :warnings true
      :pretty-print true}}]})
