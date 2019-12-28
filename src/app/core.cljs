(ns ^:figwheel-hooks app.core
  (:require [app.render :as render]
            [app.state-handler :as ne-rf]))

(defn page [db]
  [:div
   [:button {:onclick #(prn "Click")} "Click"]
   [:h1  "1"]
   [:div
    [:h1 "#" (apply str (when (zero? (rand-int 2)) (take (rand-int 30) (repeat "#"))))]]
   [:div [:canvas {:width (rand-int 500) :height (rand-int 500) :style "border:10px solid #000000;"}]]
   ])

(defonce a (js/setInterval #(render/mount (page)
                                          (js/document.querySelector "#app"))
                           0))

(defn mount [])

(defn ^:after-load re-render [] (mount))

