(ns ui.about.view
  (:require [ui.pages             :as pages]
            [ui.about.model       :as model]
            [reitit.frontend.easy :refer [href]]))

(pages/reg-subs-page
 model/index-page
 (fn [page]
   [:<>
    [:p (page :foo)]
    [:a {:href (href :ui.home.model/index)} "Home"]
    [:p model/index-title]]))
