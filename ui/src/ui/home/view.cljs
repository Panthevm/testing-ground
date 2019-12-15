(ns ui.home.view
  (:require [ui.pages             :as pages]
            [ui.home.model        :as model]
            [reitit.frontend.easy :refer [href]]))

(pages/reg-subs-page
 model/index-page
 (fn [page]
   [:<>
    [:p (page :foo)]
    [:a {:href (href :ui.about.model/index)} "About"]
    [:p model/index-title]]))
