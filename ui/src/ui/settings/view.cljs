(ns ui.settings.view
  (:require [re-frame.core         :as rf]
            [ui.settings.model     :as model]
            [ui.style              :as styles]))

(defn Visual []
  (let [dark-theme @(rf/subscribe [::styles/dark-theme])]
    [:<>
     [:h3 "Визуальные настройки"]
     [:button {:on-click #(rf/dispatch [::styles/dark-theme])}
      (if dark-theme "Дневной режим" "Ночной режим")]]))

(defn index []
  [:<>
   [:h1 model/index-title]
   [Visual]])
