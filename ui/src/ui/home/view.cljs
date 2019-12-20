(ns ui.home.view
  (:require [ui.home.model :as model]))

(defn index []
  [:<>
   [:div.row {:style {:padding-bottom "1000px"}}
    [:div.col [:span "1"]]
    [:div.col [:span "2"]]
    [:div.col [:span "3"]]]
   [:p model/index-title]])
