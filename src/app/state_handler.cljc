(ns app.state-handler)

(def state (atom nil))
(def subs (atom nil))
(def ssubs (atom nil))
(def events (atom nil))

(add-watch state :watcher
  (fn [key atom old-state new-state]
    new-state))

(defn reg-event-db [k f]
  (swap! events assoc k f))

(defn reg-ssub [k f]
  (swap! ssubs assoc k {:f f}))

(defn reg-sub [k & args]
  (swap! subs assoc k {:f (last args)
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
      @subs)))

(comment
  (do (clojure.pprint/pprint @ssubs)
      (clojure.pprint/pprint @subs)
      (clojure.pprint/pprint @events)
      (clojure.pprint/pprint @state))

  (do (reset! state {:name "Anna"
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
          (str "some: " v))))

  )
