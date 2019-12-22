(ns frames.routing
  "Copy -> https://github.com/metosin/reitit/blob/master/modules/reitit-frontend/src/reitit/frontend/history.cljs"
  (:require [reitit.core   :as reitit]
            [re-frame.core :as rf]
            [goog.events   :as gevents])
  (:import goog.Uri))

(defonce history (atom nil))

(defprotocol History
  (-init [this] "Create event listeners")
  (-stop [this] "Remove event listeners")
  (-on-navigate [this path])
  (-get-path [this])
  (-href [this path]))

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
          this (assoc this :last-fragment last-fragment)
          handler (fn [e]
                    (let [path (-get-path this)]
                      (when (or (= goog.events.EventType.POPSTATE (.-type e))
                                (not= @last-fragment path))
                        (-on-navigate this path))))]
      (-on-navigate this (-get-path this))
      (assoc this
             :popstate-listener (gevents/listen js/window goog.events.EventType.POPSTATE handler false)
             :hashchange-listener (gevents/listen js/window goog.events.EventType.HASHCHANGE handler false))))
  (-stop [this]
    (gevents/unlistenByKey popstate-listener)
    (gevents/unlistenByKey hashchange-listener))
  (-on-navigate [this path]
    (reset! last-fragment path)
    (on-navigate (match-by-path router path) this))
  (-get-path [this]
    (let [fragment (subs (.. js/window -location -hash) 1)]
      (if (= "" fragment)
        "/"
        fragment)))
  (-href [this path]
    (if path
      (str "#" path))))


(defn- event-target [event]
  (let [original-event (.getBrowserEvent event)]
    (if (exists? (.-composedPath original-event))
      (aget (.composedPath original-event) 0)
      (.-target event))))

(defn- closest-by-tag [el tag]
  (let [tag (.toUpperCase tag)]
    (loop [el el]
      (if el
        (if (= tag (.-nodeName el))
          el
          (recur (.-parentNode el)))))))

(defrecord Html5History [on-navigate router listen-key click-listen-key]
  History
  (-init [this]
    (let [handler
          (fn [e]
            (-on-navigate this (-get-path this)))

          ignore-anchor-click-predicate (:ignore-anchor-click? this)
          ignore-anchor-click (fn [e]
                                (when-let [el (closest-by-tag (event-target e) "a")]
                                  (let [uri (.parse Uri (.-href el))]
                                    (when (ignore-anchor-click-predicate router e el uri)
                                      (.preventDefault e)
                                      (let [path (str (.getPath uri)
                                                      (when (.hasQuery uri)
                                                        (str "?" (.getQuery uri)))
                                                      (when (.hasFragment uri)
                                                        (str "#" (.getFragment uri))))]
                                        (.pushState js/window.history nil "" path)
                                        (-on-navigate this path))))))]
      (-on-navigate this (-get-path this))
      (assoc this
             :listen-key (gevents/listen js/window goog.events.EventType.POPSTATE handler false)
             :click-listen-key (gevents/listen js/document goog.events.EventType.CLICK ignore-anchor-click))))
  (-on-navigate [this path]
    (on-navigate (match-by-path router path) this))
  (-stop [this]
    (gevents/unlistenByKey listen-key)
    (gevents/unlistenByKey click-listen-key))
  (-get-path [this]
    (str (.. js/window -location -pathname)
         (.. js/window -location -search)))
  (-href [this path]
    path))

(defn start!
  ([router on-navigate]
   (start! router on-navigate nil))
  ([router
    on-navigate
    {:keys [use-fragment]
     :or {use-fragment true}
     :as opts}]
   (let [opts (-> opts
                  (dissoc :use-fragment)
                  (assoc :router router
                         :on-navigate on-navigate))]
     (-init (if use-fragment
              (map->FragmentHistory opts)
              (map->Html5History opts))))))


(defn stop! [history]
  (if history
    (-stop history)))

(defn start
  [router on-navigate opts]
  (swap! history (fn [old-history]
                   (stop! old-history)
                   (start! router on-navigate opts))))

(defn init [routes]
  (-> routes
      reitit/router
      (start!
       (fn [match] (rf/dispatch [::set match]))
       {:use-fragment true})))

(rf/reg-event-db
 ::set
 (fn [db [_ match]]
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
