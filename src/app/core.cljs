(ns ^:figwheel-hooks app.core
  (:require [app.render :as render]
            [app.state-handler :as ne-rf]))

(defn page [db]
  [:div
   [:h1
    "!@#"]])

(defn layout []
  (let [db {:h1   "h1"
            :page "page"}]
    [:div
     [:h1 (:h1 db)]
     [page]]))

(defn mount []
  (render/render [layout]
                           (js/document.querySelector "#app")))

(defn ^:after-load re-render [] (mount))

