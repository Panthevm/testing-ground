(ns ui.home.view
  (:require [ui.home.model :as model]))

(defn index []
  [:<>
   [:a {:href "#/a"} "About"]
   [:p model/index-title]])
