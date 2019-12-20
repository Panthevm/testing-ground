(ns ui.components.buttons
  (:require [re-frame.core  :as rf]
            [ui.routes      :as routes]
            [clojure.string :as str]))

(defn icon
  [{:keys [icon active] :as attrs}]
  [:button (dissoc attrs :icon)
   [:span
    (if active (first icon) (last icon))]])

(defn link []
  (let [fragment (rf/subscribe [::routes/fragment])]
    (when @fragment
      (fn [attr]
        (let [attr (if (->> attr :href (str/includes? @fragment))
                     (update attr :class str " active")
                     attr)]
          [:a.block attr
           (:title attr)])))))
