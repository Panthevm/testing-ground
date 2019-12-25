(ns build
  (:require [cljs.build.api :as api]))

(def source-dir "src")

(def compiler-config
  {:output-to      "resources/public/js/app.js"
   :output-dir     "resources/public/js/out"
   :pretty-print   false
   :main           'ui.prod
   :optimizations  :advanced})

(defn -main []
  (api/build source-dir compiler-config))
