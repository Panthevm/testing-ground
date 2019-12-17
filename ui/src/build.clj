(ns build
  (:require [cljs.build.api :as api]))

(def source-dir "src/ui")

(def compiler-config
  {:output-to      "resources/public/js/app.js"
   :output-dir     "resources/public/js/out"
   :pretty-print   false
   :parallel-build true
   :verbose        true
   :optimizations  :advanced
   :static-fns     true
   :main           'ui.prod})

(defn -main []
  (api/build source-dir compiler-config))
