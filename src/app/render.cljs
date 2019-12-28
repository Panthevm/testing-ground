(ns app.render
  (:require [app.dom    :as dom]
            [app.state-handler :as state]
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
    (hiccup/conform ((first v)))
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

(def ctx (merge (state/get-init-subs)
                {:h1   "h1"
                 :page "page"}))

(defn p [v] (prn v) v)

(def hiccup-dom (atom nil))

(defn save-dom [view]
  (let [dom (clojure.walk/prewalk #(if (and (:subs (meta %)) (fn? %))
                                     (with-meta (% ctx) (assoc (meta %) :f %))
                                     %) view)]
    (reset! hiccup-dom dom)
    dom))

(defn render [view root]
  (let [old @vdom]
    (->> view
         save-dom
         p
         hiccup/conform
         (reset! vdom)
         (update-element root old))))

(defn prewalk-update [view ctx]
  (prn "prewalk-update ctx" ctx)
  (prn "prewalk-update " view)
  (let [prew-res (clojure.walk/prewalk #(if (and (:subs (meta %)) (fn? %))
                                          (with-meta (% ctx) (assoc (meta %) :f %))
                                          %) view)]
    (prn "prew result: " prew-res)
    prew-res))

(add-watch state/render-atom
           :render
           (fn [_ _ _ new-state]
             (println "rerender")
             (println "new-state: " new-state)
             (let [old @vdom
                   new-state (merge new-state {:h1 "h1" :page "page"})]
               (->> @hiccup-dom
                    (clojure.walk/prewalk #(do
                                             (if-let [subs (seq (:subs (meta %)))]
                                               (do (prn "subs: " subs)
                                                   (if (some (fn [k] (contains? (set subs) k)) (keys new-state))
                                                     (do
                                                       (prn "contains")
                                                       (with-meta (prewalk-update ((:f (meta %)) new-state) new-state) (meta %)))
                                                     %))
                                               %)))
                    p
                    (reset! hiccup-dom)
                    hiccup/conform
                    (reset! vdom)
                    (update-element (js/document.querySelector "#app") old)))))
