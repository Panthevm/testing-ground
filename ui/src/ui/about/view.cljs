(ns ui.about.view
  (:require
   [re-frame.core :as rf]
   [ui.about.model       :as model]))

(defn index []
  (let [page @(rf/subscribe [model/index-page])]
    [:<>
     [:h1 (:foo page)]
     [:p model/index-title]]))
