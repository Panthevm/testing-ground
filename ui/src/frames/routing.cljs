(ns frames.routing
  (:require [reitit.core   :as reitit]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [goog.events   :as gevents]))

(defn location->path [location]
  (-> location
      (str/split #"/")
      next
      (->> (map #(keyword %)))
      vec))

(defn match-path
  [routes]
  (let [window-location (.. js/window -location -hash)
        path (-> window-location location->path)
        path (if (empty? path) [:.] path)]
    {:page (get-in routes path)
     :path window-location}))

(defn fragment-history [routes]
  (let [on-navigate (fn [] (rf/dispatch [::set (match-path routes)]))
        listen      (fn [] (gevents/listen    js/window "hashchange" on-navigate))
        unlisten    (fn [] (gevents/removeAll js/window "hashchange"))]
    (on-navigate)
    (unlisten)
    (listen)))

(defn init [routes]
  (-> routes
      #_reitit/router
      fragment-history))

(rf/reg-event-db
 ::set
 (fn [db [_ match]]
   (assoc db :route {:fragment (:path match)
                     :page     (:page match)})))

(rf/reg-sub
 ::current
 (fn [db]
   (get db :route)))

(rf/reg-sub
 ::fragment
 (fn [db]
   (get-in db [:route :fragment])))
