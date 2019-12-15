(ns ui.routes
  (:require [re-frame.core               :as rf]
            [ui.pages                    :as pages]
            [reitit.frontend             :as rfrontend]
            [reitit.frontend.easy        :as reasy]
            [reitit.frontend.controllers :as rfc]))

(def routes
  ["/"
   [""   {:name        :ui.home.model/index
          :controllers [{:start (fn [] (prn "start" "index"))
                         :stop  (fn [] (prn "stop" "index"))}]}]
   ["a"  {:name        :ui.about.model/index
          :controllers [{:start (fn [] (prn "start" "about"))
                         :stop  (fn [] (prn "stop" "about"))}]}]])

(rf/reg-sub
 ::get
 (fn [db]
   (get db ::current)))

(rf/reg-event-db
 ::set
 (fn [db [_ {:keys [path data]}]]
   (assoc db ::current {:path path
                        :name (:name data)})))

(defn init []
  (reasy/start!
   (rfrontend/router routes)
   (fn [new]
     (update @pages/pages (get-in new [:data :name])
             (fn [old]
               (when new
                 (assoc new
                        :controllers (rfc/apply-controllers (:controllers old) new)))))
     (rf/dispatch [::set new]))
   {:use-fragment true}))
