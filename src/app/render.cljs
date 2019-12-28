(ns app.render
  (:require [app.dom    :as dom]
            [app.hiccup :as hiccup]))

(defonce vdom (atom nil))

(defn changed? [[type-o old] [type-n new]]
  (and (= (:tag old) (:tag new))
       (or (not= :content type-n) (= old new))
       (= type-o type-n)
       (= (:attr old) (:attr new))))

(defn update-element [parent old new & [key]]
  (let [[o-type o-attr] old
        [n-type n-attr] new]
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

(defn mount [new parent]
  (let [old @vdom]
    (->> new
         hiccup/conform
         (reset! vdom)
         (update-element parent old))))
