(ns build
  (:require [cljs.build.api :as api]))

(def source-dir "src")

(def compiler-config
  {:output-to     "resources/public/js/app.js"
   :pretty-print  false
   :optimizations :advanced
   :main          'ui.prod})

(defn -main []
  (api/build source-dir compiler-config))
