(ns ui.components.layout
  (:require [ui.style       :as styles]
            [re-frame.core  :as rf]
            [clojure.string :as str]
            [ui.helpers     :as h]))

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
             {:title "О нас"   :href "#/about"}]
     fragment (current-nav fragment))))

(defn layout []
  (let [links (rf/subscribe [::menu])
        open? (rf/subscribe [::styles/expands :navbar])]
    (fn [body]
      [:<> styles/app
       [:nav (when @open? {:class "nav-expand"})
        [:button {:on-click #(rf/dispatch [::styles/expands :navbar])}
         (if @open? "X" "=")]
        (map-indexed
         (fn [idx link] ^{:key idx}
           [:a.block link (:title link)])
         @links)]
       [:div.container body]])))
