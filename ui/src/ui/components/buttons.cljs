(ns ui.components.buttons
  (:require [re-frame.core  :as rf]
            [clojure.string :as str]))

(defn icon
  [{:keys [icon active] :as attrs}]
  [:button (dissoc attrs :icon)
   (if active (first icon) (last icon))])

(defn action
  [attrs]
  [:button (dissoc attrs :icon)
   [:span (:text attrs)]])

(defn url-matches?
  [url match]
  (= (-> url (str/split #"\?") first)
     match))

(defn link []
  (let [fragment (rf/subscribe [:fragment])]
    (when @fragment
      (fn [{:keys [href] :as attr}]
        (let [attr (if (url-matches? href @fragment)
                     (update attr :class str " active")
                     attr)]
          [:a.block attr
           (:title attr)])))))
