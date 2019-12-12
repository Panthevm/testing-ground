(ns ^:figwheel-hooks ui.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn root []
  [:p "HELLO World"])

(rf/reg-event-fx
 ::initialize
 (fn []
   {:db {:a 1}}))

(defn mount []
  (rf/dispatch-sync [::initialize])
  (reagent/render [root] (js/document.getElementById "app")))

(defn ^:after-load re-render [] (mount))
