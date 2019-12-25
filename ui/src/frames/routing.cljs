(ns frames.routing
  (:require [re-frame.core  :as rf]
            [clojure.string :as str]
            [goog.events    :as gevents]))

(defn location->path [location]
  (-> location
      (str/split #"/")
      next
      (->> (mapv keyword))
      not-empty))

(defn match-path
  [routes]
  (let [location (.. js/window -location -hash)
        path     (-> location location->path (or [:.]))]
    {:page (get-in routes path)
     :path location}))

(defn routing [routes]
  (let [on-navigate (fn [] (rf/dispatch [::set (match-path routes)]))
        listen      (fn [] (gevents/listen    js/window "hashchange" on-navigate))
        unlisten    (fn [] (gevents/removeAll js/window "hashchange"))]
    (on-navigate)
    (unlisten)
    (listen)))

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
