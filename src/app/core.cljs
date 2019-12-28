(ns ^:figwheel-hooks app.core
  (:require [app.render :as render]))

(defn page []
  [:div {:style "color: red"}
   [:button {:onclick #(prn "Click")} "Click"]
   [:h1  "1"]
   [:h1 "#" (apply str (when (zero? (rand-int 2)) (take (rand-int 30) (repeat "#"))))]
   [:h1 "#" (apply str (when (zero? (rand-int 2)) (take (rand-int 30) (repeat "#"))))]
   [:h1 "#" (apply str (when (zero? (rand-int 2)) (take (rand-int 30) (repeat "#"))))]
   [:h1 "#" (apply str (when (zero? (rand-int 2)) (take (rand-int 30) (repeat "#"))))]
   [:h1 "#" (apply str (when (zero? (rand-int 2)) (take (rand-int 30) (repeat "#"))))]
   [:h1 "#" (apply str (when (zero? (rand-int 2)) (take (rand-int 30) (repeat "#"))))]
   [:h1 "#" (apply str (when (zero? (rand-int 2)) (take (rand-int 30) (repeat "#"))))]
   [:h1 "#" (apply str (when (zero? (rand-int 2)) (take (rand-int 30) (repeat "#"))))]
   [:h1 "2"]])

(defonce a (js/setInterval #(render/mount (page)
                                          (js/document.querySelector "#app"))
                           0))

(defn mount [])

(defn ^:after-load re-render [] (mount))
