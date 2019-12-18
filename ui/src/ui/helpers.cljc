(ns ui.helpers
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::db
 (fn [db [_ pid]]
   (get-in db pid)))
