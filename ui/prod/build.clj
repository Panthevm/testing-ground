(ns build
  (:require [cljs.build.api :as api]))

(def source-dir "src/ui")

(def compiler-config
  {:output-to      "resources/public/js/app.js"
   :output-dir     "resources/public/js/out"
   :cache-analysis false
   :pretty-print   false
   :verbose        false
   :specs          false
   :main           'ui.prod
   :optimizations  :advanced})

(defn -main []
  (api/build source-dir compiler-config))
