(ns ui.about.model
  (:require [re-frame.core :as rf]))

(def index-page  ::index)
(def index-title "О нас")

(rf/reg-sub
 index-page
 (fn [_]
   {:foo 123}))
