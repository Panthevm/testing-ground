(ns ui.settings.model
  (:require [re-frame.core :as rf]))

(def index-page  ::index)
(def index-title "Настройки")

(rf/reg-sub
 index-page
 (fn [_]
   {:foo 123}))
