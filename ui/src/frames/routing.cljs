(ns frames.routing
  "Copy -> https://github.com/metosin/reitit/blob/master/modules/reitit-frontend/src/reitit/frontend/history.cljs"
  (:require [reitit.core   :as reitit]
            [re-frame.core :as rf]
            [goog.events   :as gevents])
  (:import goog.Uri))

(defonce history (atom nil))
(defonce match   (atom nil))

(defprotocol History
  (-init        [this])
  (-stop        [this])
  (-on-navigate [this path])
  (-get-path    [this]))

(defn- query-param [^goog.Uri.QueryData q k]
  (let [vs (.getValues q k)]
    (if (< (alength vs) 2)
      (aget vs 0)
      (vec vs))))

(defn query-params
  [^goog.Uri uri]
  (let [q (.getQueryData uri)]
    (->> q
         (.getKeys)
         (map (juxt keyword #(query-param q %)))
         (into {}))))

(defn match-by-path
  [router path]
  (let [uri (.parse Uri path)]
    (if-let [match (reitit/match-by-path router (.getPath uri))]
      (let [q (query-params uri)
            match (assoc match :query-params q)
            parameters {:path (:path-params match)
                        :query q}]
        (assoc match :parameters parameters)))))

(defrecord FragmentHistory [on-navigate router popstate-listener hashchange-listener last-fragment]
  History
  (-init [this]
    (let [last-fragment (atom nil)
          this          (assoc this :last-fragment last-fragment)
          handler       (fn []
                          (let [path (-get-path this)]
                            (when (not= @last-fragment path)
                              (-on-navigate this path))))]
      (-on-navigate this (-get-path this))
      (assoc this
             :popstate-listener   (gevents/listen js/window goog.events.EventType.POPSTATE   handler false)
             :hashchange-listener (gevents/listen js/window goog.events.EventType.HASHCHANGE handler false))))
  (-stop [this]
    (gevents/unlistenByKey popstate-listener)
    (gevents/unlistenByKey hashchange-listener))
  (-on-navigate [this path]
    (reset! last-fragment path)
    (on-navigate (match-by-path router path) this))
  (-get-path [this]
    (let [fragment (subs (.. js/window -location -hash) 1)]
      (if (= "" fragment) "/" fragment))))

(defn hooks
  [{{init-new :init} :data :as new}]
  (swap! match (fn [{{deinit-old :deinit} :data :as old}]
                 (when new
                   (init-new)
                   (when old (deinit-old))
                   new))))

(defn start!
  [router on-navigate]
  (-init (map->FragmentHistory {:router      router
                                :on-navigate on-navigate})))

(defn stop! [history]
  (when history
    (-stop history)))

(defn navigate-action
  [router on-navigate]
  (swap! history (fn [old-history]
                   (stop! old-history)
                   (start! router on-navigate))))

(defn init [routes]
  (-> routes
      reitit/router
      (navigate-action
       (fn [match]
         (hooks match)
         (rf/dispatch [::set match])))))

(rf/reg-event-db
 ::set
 (fn [db [_ match]]
   (prn "::set")
   (assoc db :route {:fragment (str "#" (:path match))
                     :page     (get-in match [:data :view])})))

(rf/reg-sub
 ::current
 (fn [db]
   (get db :route)))

(rf/reg-sub
 ::fragment
 (fn [db]
   (get-in db [:route :fragment])))
