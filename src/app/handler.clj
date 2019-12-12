(ns app.handler
  (:require [reitit.ring :as ring]))

(def handler
  (ring/ring-handler
   (ring/router
    [["/t" {:get {:handler (fn [req] {:status 200
                                     :body   (str req)})}}]])
   (ring/create-default-handler)))
