(ns ^:figwheel-hooks ui.core
  (:require [re-frame.core        :as rf]
            [reagent.core         :as reagent]
            [ui.routes            :as routes]
            [frames.routing       :as routing]
            [ui.components.layout :as layout]))

(defn current-page []
  (let [page (rf/subscribe [::routing/current])]
    (fn []
      (let [page (:page @page)]
        [layout/layout
         (if page
           [page]
           [:div "Страница не найдена"])]))))

(defn mount []
  (routing/routing routes/routes)
  (reagent/render [current-page] (js/document.getElementById "app")))

(defn ^:after-load re-render [] (mount))
