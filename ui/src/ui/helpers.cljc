(ns ui.helpers
  (:require [re-frame.core  :as rf]))

(rf/reg-sub
 ::expands
 (fn [db [_ key]]
   (get-in db [:open key])))

(rf/reg-event-db
 ::expands
 (fn [db [_ key]]
   (update-in db [:open key] not)))
