(ns app.core
  (:require [immutant.web :as web]
            [app.handler  :as handler])
  (:gen-class))

(def app
  #'handler/handler)

(defn -main
  [& _]
  (web/run app {:host "localhost" :port 8080 :path "/"}))

(comment
  (web/stop))
