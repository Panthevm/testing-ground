(ns app.state-handler)

(def render-atom (atom nil))
(def state (atom nil))
(def *subs (atom nil))
(def ssubs (atom nil))
(def events (atom nil))

(add-watch
 state :watcher
 (fn [_ _ old-state new-state]
   (prn "watcher")
   (let [ssubs @ssubs
         subscriptions @*subs
         all-ssubs (map
                     (fn [[k {:keys [f]}]]
                       {:key k
                        :new (f new-state)
                        :old (f old-state)})
                     ssubs)
         all-reduced
         (reduce
           (fn [a v]
             (assoc a (:key v) v))
           {}
           all-ssubs)
         changed (remove (fn [{:keys [new old]}]
                           (= new old))
                         all-ssubs)
         changed-reduced
         (reduce
           (fn [a v]
             (assoc a (:key v) v))
           {}
           changed)
         changed-ss (filter (fn [[_ {:as v :keys [subs]}]]
                              (some (partial get changed-reduced) subs))
                            subscriptions)
       res
       (reduce-kv
         (fn [acc k {:keys [f subs]}]
           (assoc acc k (apply f (map #(get-in all-reduced [% :new])
                                      subs))))
         {}
         (reduce (fn [a [k v]] (assoc a k v)) {} changed-ss))]
     (prn res)
     (reset! render-atom res))))

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

(reset! state {:name "Anna"
               :some "321"
               :amount {:value 1}})

(do (reset! state {:name "Anna"
                   :some "321"
                   :amount {:value 1}})
    (reg-event-db
      :inc-state
      (fn [db] (swap! db update-in [:amount :value] inc)))

    (reg-event-db
      :change-some
      (fn [db] (swap! db assoc :some (rand))))

    (reg-event-db
     :input
     (fn [db value] (swap! db assoc :input value)))

    (reg-ssub
     :->input
     (fn [db] (get db :input)))

    (reg-sub
     :form
     :->input
     (fn [v] v))

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
        (str "some: " v))))

