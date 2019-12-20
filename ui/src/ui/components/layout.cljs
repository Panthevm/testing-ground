(ns ui.components.layout
  (:require [ui.style              :as styles]
            [re-frame.core         :as rf]
            [ui.components.buttons :as buttons]))

(def menu
  [{:title "Главная" :href "#/home"}
   {:title "О нас"   :href "#/about"}])

(defn layout []
  (let [open? (rf/subscribe [::styles/expands :navbar])]
    (fn [body]
      [:<> [styles/app]
       [:nav (when @open? {:class "nav-expand"})
        [buttons/icon {:on-click #(rf/dispatch [::styles/expands :navbar])
                       :icon     [[:span "X"]]}]
        (map-indexed
         (fn [idx attrs] ^{:key idx}
           [buttons/link (assoc attrs :class "link")])
         menu)
        [buttons/icon {:on-click #(rf/dispatch [::styles/dark-theme])
                       :icon     [[:img {:src "icon/menu.svg"}]]}]]
       [buttons/icon {:on-click #(rf/dispatch [::styles/expands :navbar])
                      :class    "fixed shadow"
                      :icon     [[:img {:src "icon/menu.svg"}]]}]
       [:div.container body]])))
