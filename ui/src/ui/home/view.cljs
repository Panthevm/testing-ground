(ns ui.home.view
  (:require [ui.home.model :as model]))

(defn index []
  [:<>
   [:p model/index-title]])
