(ns ui.routes
  (:require [re-frame.core        :as rf]
            [reitit.frontend      :as reitit]
            [reitit.frontend.easy :as easy]

            [ui.home.view     :as home]
            [ui.settings.view :as settings]))

(def routes
  ["/"
   [""          {:view #'home/index}]
   ["settings"  {:view #'settings/index}]])

(defn init []
  (-> routes
      reitit/router
      (easy/start!
       (fn [match] (rf/dispatch [::set match]))
       {:use-fragment true})))

(rf/reg-sub
 ::current
 (fn [db]
   (get db :route)))

(rf/reg-sub
 :fragment
 (fn [db]
   (get-in db [:route :fragment])))

(rf/reg-event-db
 ::set
 (fn [db [_ match]]
   (assoc db :route {:fragment (str "#" (:path match))
                     :page     (get-in match [:data :view])})))
