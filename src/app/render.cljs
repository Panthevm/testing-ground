(ns app.render
  (:require [app.dom    :as dom]
            [app.state-handler :as rf]
            [app.hiccup :as hiccup]))

(defonce vdom (atom nil))

(defn changed? [[type-o old] [type-n new]]
  (or (not= (:tag old) (:tag new))
      (not= type-o type-n)
      (not= (select-keys (:attr old) [:style :id :class :width :height])
            (select-keys (:attr new) [:style :id :class :width :height]))
      (and (= :content type-n) (not= old new))))

(defn component? [[type v]]
  (if (fn? (first v))
    (do
      (rf/reg-sub :sub (first v))
      (hiccup/conform ((first v))))
    [type v]))

(defn update-element [parent old new & [key]]
  (let [[o-type o-attr] (component? old)
        [n-type n-attr] (component? new)]
    (cond
      (= new old)         nil
      (nil? old)          (dom/append-child  parent new)
      (nil? new)          (dom/remove-child  parent key)
      (changed? old new)  (dom/replace-child parent new key)
      (= :element n-type) (let [{n-child :children} n-attr
                                {o-child :children} o-attr]
                            (doall (map
                                    (fn [idx]
                                      (update-element
                                       (dom/nodes-idx parent key)
                                       (get o-child idx)
                                       (get n-child idx)
                                       idx))
                                    (range (max (count n-child)
                                                (count o-child)))))))))


(defn render [comp container]
  (let [old @vdom]
    (->> comp
         ((fn [a] ((first a))))
         hiccup/conform
         (reset! vdom)
         (update-element container old))))
