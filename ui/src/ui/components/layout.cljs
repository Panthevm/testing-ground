(ns ui.components.layout
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [ui.helpers    :as h]))

(defn current-nav [fragment navs]
  (map
   (fn [link]
     (if (str/includes? fragment (:href link))
       (assoc link :class "active font-weight-bold")
       link))
   navs))

(rf/reg-sub
 ::menu
 :<- [::h/db [:route :path]]
 (fn [fragment]
   (cond->> [{:title "Главная" :href "#/"}
             {:title "О нас" :href "#/about"}]
     fragment (current-nav fragment))))

(defn layout []
  (let [links (rf/subscribe [::menu])]
    (fn [body]
      [:<>
       [:div (map-indexed
              (fn [idx {:keys [title href]}] ^{:key idx}
                [:a {:href href} title])
              @links)]
       [:div body]])))
