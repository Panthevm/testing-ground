(ns app.state-handler)

(def state (atom nil))

(def subs (atom nil))

(def events (atom nil))

(add-watch state :watcher
  (fn [key atom old-state new-state]
    (let [db @state]
      (doseq [s (vals @subs)]
        (s db)))))

(defn reg-event-db [k f]
  (swap! events assoc-in k f))

(defn reg-sub [k f]
  (swap! subs assoc k f))

(defn dispatch [[k & args]]
  (apply (get @events k) state args))

(comment
  (reg-sub :sub2 (fn [db]
                   (println "I am sub2")
                   db))
  (reg-event-db
    :inc-state
    (fn [db] (swap! db update :value inc)))

  (dispatch [:inc-state])
  (clojure.pprint/pprint @subs)
  (clojure.pprint/pprint @events)
  (clojure.pprint/pprint @state))
