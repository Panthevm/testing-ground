(ns ui.home.view
  (:require [ui.pages      :as pages]
            [ui.home.model :as model]))

(pages/reg-subs-page
 model/index-page
 (fn [page]
   [:<>
    [:p (page :foo)]
    [:p model/index-title]]))
