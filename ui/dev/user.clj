(ns user
  (:require [figwheel.main.api :as figwheel]))

(def options
  {:id      "app"
   :options {:main       'ui.dev
             :output-to  "resources/public/js/app.js"
             :output-dir "resources/public/js/out"}
   :config  {:watch-dirs          ["src"]
             :mode                :serve
             :ring-server-options {:port 3449}}})

(defonce state
  (future (figwheel/start options)))

(comment
  (figwheel/cljs-repl "app"))
