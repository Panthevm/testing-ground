(ns ^:figwheel-hooks ui.core
  (:require [reagent.core  :as reagent]
            [re-frame.core :as rf]
            [ui.pages      :as pages]
            [ui.routes     :as routes]

            [ui.home.view]))

(defn current-page []
  (let [route (rf/subscribe [::routes/get])]
    (fn []
      (let [page (get @pages/pages (:page @route))]
        (if page
          [page]
          [:div "Страница не найдена"])))))

(defn mount []
  (routes/init)
  (reagent/render [current-page] (js/document.getElementById "app")))

(defn ^:after-load re-render [] (mount))
