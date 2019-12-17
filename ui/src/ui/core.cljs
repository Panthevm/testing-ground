(ns ^:figwheel-hooks ui.core
  (:require [reagent.core    :as reagent]
            [ui.routes       :as routes]
            [reagent.session :as session]))

(defn current-page []
  (fn []
    (let [page (:page (session/get :route))]
      (if page
        [page]
        [:div "Страница не найдена"]))))

(defn mount []
  (routes/init)
  (reagent/render [current-page] (js/document.getElementById "app")))

(defn ^:after-load re-render [] (mount))
