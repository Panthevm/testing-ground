(ns ui.settings.view
  (:require [re-frame.core         :as rf]
            [ui.settings.model     :as model]
            [ui.components.buttons :as buttons]
            [ui.style              :as styles]))

(defn Visual []
  (let [dark-theme (rf/subscribe [::styles/dark-theme])]
    [:<>
     [:h3 "Визуальные настройки"]
     [buttons/action {:on-click #(rf/dispatch [::styles/dark-theme])
                      :text (if dark-theme "Дневной режим"
                                "Ночной режим")}]]))

(defn index []
  [:<>
   [:h1 model/index-title]
   [Visual]])
