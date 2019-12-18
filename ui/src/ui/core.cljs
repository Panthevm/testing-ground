(ns ^:figwheel-hooks ui.core
  (:require [re-frame.core :as rf]
            [reagent.core    :as reagent]
            [ui.routes       :as routes]

            [ui.components.layout :as layout]))

(defn current-page []
  (let [page (rf/subscribe [::routes/current])]
    (fn []
      (let [page (:page @page)]
        (if page
          [layout/layout
           [page]]
          [:div "Страница не найдена"])))))

(defn mount []
  (routes/init)
  (reagent/render [current-page] (js/document.getElementById "app")))

(defn ^:after-load re-render [] (mount))
