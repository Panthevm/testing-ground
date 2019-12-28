(ns app.render
  (:require [app.dom    :as dom]
            #_[app.state-handler :as state]
            [app.hiccup :as hiccup])
  )
(def state (atom nil))
(def *subs (atom nil))
(def ssubs (atom nil))
(def events (atom nil))

(add-watch
 state :watcher
 (fn [old-state new-state]
   (let [ssubs @ssubs
         subs @*subs
         changed (remove (fn [{:keys [new old]}]
                           (= new old))
                         (map
                          (fn [[k {:keys [f]}]]
                            {:key k
                             :new (f new-state)
                             :old (f old-state)})
                          ssubs))
         sssssssubs (reduce
                     (fn [a v]
                       (assoc a (:subs v) v))
                     {}
                     subs)
         ]
     (map
      (fn [{:keys [key]}]
        )
      changed)
     )
   new-state))

(defn reg-event-db [k f]
  (swap! events assoc k f))

(defn reg-ssub [k f]
  (swap! ssubs assoc k {:f f}))

(defn reg-sub [k & args]
  (swap! *subs assoc k {:f (last args)
                       :subs (drop-last args)}))

(defn dispatch [[k & args]]
  (apply (get @events k) state args))

(defn get-init-subs []
  (let [db @state
        ssubs @ssubs]
    (reduce-kv
     (fn [acc k {:keys [f subs]}]
        (assoc acc k (apply f (map (fn [{:keys [f]}]
                                     (f db))
                                   (map #(% ssubs) subs)))))
      {}
      @*subs)))

(defonce s (do (reset! state {:name "Anna"

                            :some "321"
                            :amount {:value 1}})

             (reg-event-db
              :inc-state
              (fn [db] (swap! db update-in [:amount :value] inc)))

             (reg-ssub
              :->amount
              (fn [db] (get-in db [:amount :value])))

             (reg-ssub
              :->name
              (fn [db] (:name db)))

             (reg-ssub
              :->some-comp-sub
              (fn [db] (:some db)))


             (reg-sub
              :sub1
              :->amount
              :->name
              (fn [amount name]
                (str amount name)))

             (reg-sub
              :some-comp-sub
              :->some-comp-sub
              (fn [v]
                (str "some: " v)))))

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
      (reg-sub :sub (first v))
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

(def ctx (merge (get-init-subs)
                {:h1   "h1"
                 :page "page"}))

(defn render [view root]
  (let [old @vdom]
    (->> view
         (clojure.walk/prewalk #(if (fn? %) (% ctx) %))
         hiccup/conform
         (reset! vdom)
         (update-element root old))))

