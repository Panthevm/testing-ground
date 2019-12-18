(ns ui.home.view
  (:require [ui.home.model :as model]))

(defn index []
  [:<>
   [:div.row
    [:div.col "1"]
    [:div.col "2"]
    [:div.col "3"]]
   [:p model/index-title]])
