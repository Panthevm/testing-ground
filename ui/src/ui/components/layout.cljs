(ns ui.components.layout
  (:require [ui.style       :as styles]
            [re-frame.core  :as rf]
            [ui.helpers    :as h]
            [ui.components.sidebar :as sidebar]))

(defn layout []
  (fn [body]
    [:div [styles/app]
     [sidebar/view]
     [:button.menu-open {:on-click #(rf/dispatch [::h/expands :navbar])}
      [:div.menu-icon]]
     [:div.container body]]))
