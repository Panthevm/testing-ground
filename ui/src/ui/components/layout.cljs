(ns ui.components.layout
  (:require [ui.style              :as styles]
            [re-frame.core         :as rf]
            [ui.components.buttons :as buttons]))

(def menu
  [{:title "Главная"   :href "#/"}
   {:title "Настройки" :href "#/settings"}])

(defn layout []
  (let [open? (rf/subscribe [::styles/expands :navbar])]
    (fn [body]
      [:<> [styles/app]
       [:nav (when-not @open? {:class "nav-expand"})
        [buttons/icon {:on-click #(rf/dispatch [::styles/expands :navbar])
                       :class    "menu-close"
                       :icon     [[:div.close.icon]]}]
        (map-indexed
         (fn [idx attrs] ^{:key idx}
           [buttons/link (assoc attrs :class "link")])
         menu)]
       [buttons/icon {:on-click #(rf/dispatch [::styles/expands :navbar])
                      :class    "fixed shadow menu-open"
                      :icon     [[:div.menu-icon]]}]
       [:div.container {:class (when @open? "body-wrapper")}
        body]])))
