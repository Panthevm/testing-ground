(ns app.render
  (:require [app.dom    :as dom]
            [app.state-handler :as state]
            [clojure.walk :refer [prewalk]]
            [app.hiccup :as hiccup]))

(def hiccup-dom (atom nil))

(defonce vdom (atom nil))

(defn changed? [[type-o old] [type-n new]]
  (or (not= (:tag old) (:tag new))
      (not= type-o type-n)
      (not= (:attr old)
            (:attr new))
      (and (= :content type-n) (not= old new))))

(defn component? [[type v]]
  (if (fn? (first v))
    (hiccup/conform ((first v)))
    [type v]))

(defn make-pairs [a b]
  (let [[a b] (sort-by count [a b])]
    (mapv vector (concat a (repeat nil)) b)))

(defn update-element [parent old new & [key]]
  (let [[o-type o-attr] (component? old)
        [n-type n-attr] (component? new)]
    (cond
      (nil? old)          (dom/append-child  parent new)
      (nil? new)          (dom/remove-child  parent old)
      (changed? old new)  (dom/replace-child parent old key)
      (= :element n-type) (let [{n-child :children} n-attr
                                {o-child :children} o-attr]
                            (doall (map-indexed
                                    (fn [idx [old-node new-node]]
                                      (update-element
                                       (dom/nodes-idx parent key) old-node new-node idx))
                                    (make-pairs o-child n-child)))))))

(defn prewalk-update [ctx view]
  (prewalk (fn [component]
             (if (:subs (meta component))
               (with-meta
                 (component ctx)
                 (assoc (meta component) :f component))
               component))
           view))

(defn render [view root]
  (let [old @vdom]
    (->> view
         (prewalk-update (state/get-init-subs))
         (reset! hiccup-dom)
         hiccup/conform
         (reset! vdom)
         (update-element root old))))



(defn re-render [new-state hiccup]
  (prewalk (fn [node]
             (if-let [subs (seq (:subs (meta node)))]
               (if (some (fn [k] (contains? (set subs) k))
                         (keys new-state))
                 (with-meta
                   (prewalk-update new-state ((:f (meta node)) new-state))
                   (meta node))
                 node)
               node))
           hiccup))

(add-watch
 state/render-atom
 :render
 (fn [_ _ _ new-state]
   (let [old @vdom]
     (->> @hiccup-dom
          (re-render new-state)
          (reset! hiccup-dom)
          hiccup/conform
          (reset! vdom)
          (update-element (js/document.querySelector "#app") old)))))
