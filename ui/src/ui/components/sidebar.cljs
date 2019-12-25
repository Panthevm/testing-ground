(ns ui.components.sidebar
  (:require [ui.helpers     :as h]
            [re-frame.core  :as rf]
            [clojure.string :as str]))


(defn url-matches?
  [fragment links]
  (map
   (fn [link]
     (if (= (-> link :href (str/split #"\?") first)
            fragment)
       (assoc link :class "active")
       link))
   links))

(rf/reg-sub
 ::navigation
 :<- [:frames.routing/fragment]
 (fn [fragment]
   (cond->> [{:title "Главная"   :href "#/"}
             {:title "Настройки" :href "#/settings/main"}]
     fragment (url-matches? fragment))))

(defn view []
  (let [*open? (rf/subscribe [::h/expands :navbar])
        *menu  (rf/subscribe [::navigation])]
    (fn []
      (let [open? (deref *open?)
            menu  (deref *menu)]
        [:nav (when-not open? {:class "nav-expand"})
         [:button.menu-close {:on-click #(rf/dispatch [::h/expands :navbar])}
          [:div.close.icon]]
         (map-indexed
          (fn [idx attrs] ^{:key idx}
            [:a.block.link attrs
             (:title attrs)])
          menu)]))))
