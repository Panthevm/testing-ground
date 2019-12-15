(ns ui.routes
  (:require [re-frame.core :as rf]
            [reitit.frontend :as rfrontend]
            [reitit.frontend.easy :as reasy]))

(def routes
  ["/"
   [""  {:page :ui.home.model/index}]])

(rf/reg-sub
 ::get
 (fn [db]
   (get db ::current)))

(rf/reg-event-db
 ::set
 (fn [db [_ v]]
   (assoc db ::current v)))

(defn init []
  (reasy/start!
   (rfrontend/router routes)
   (fn [{:keys [path data]}]
     (rf/dispatch [::set {:path path :page (:page data)}]))
   {:use-fragment true}))
