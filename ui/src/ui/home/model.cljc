(ns ui.home.model
  (:require [re-frame.core :as rf]))

(def index-page  ::index)
(def index-title "Главаня страница")

(rf/reg-sub
 index-page
 (fn [_]
   {:foo 123}))
