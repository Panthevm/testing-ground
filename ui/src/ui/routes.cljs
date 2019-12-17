(ns ui.routes
  (:require [reagent.session :as session]
            [reitit.frontend             :as reitit]
            [reitit.frontend.easy        :as easy]

            [ui.home.view  :as home]
            [ui.about.view :as about]))

(def routes
  ["/"
   [""   {:view #'home/index}]
   ["a"  {:view #'about/index}]])

(defn init []
  (easy/start!
   (reitit/router routes)
   (fn [match]
     (session/put! :route {:path (:path match)
                           :page (get-in match [:data :view ])}))
   {:use-fragment true}))
