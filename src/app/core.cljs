(ns ^:figwheel-hooks app.core
  (:require [app.render :as render]
            [app.state-handler :as state]))

(defn comp-without-sub [form]
  [:div (str "FORM VALUE: " form)])

(def some-comp
  (with-meta (fn [{:keys [some-comp-sub]}]
               [:div (str "123" some-comp-sub)])
    {:subs [:some-comp-sub]}))

(def some-list
  (with-meta
    (fn [{:keys [list-elemets]}]
      [:div
       [:div
        [:button {:onclick #(state/dispatch [:append-to-list])}
         "Add to list"]]
       (apply vector :div (map (fn [l] [:div (str l)]) list-elemets))])
    {:subs [:list-elemets]}))

(def page
  (with-meta
    (fn [{:keys [form] :as ss}]
      [:div
       [:button {:onclick #(state/dispatch [:inc-state])} "Inc state"]
       [:button {:onclick #(state/dispatch [:change-some])} "Change some"]
       [:input {:value form
                :onchange #(state/dispatch [:input (.. % -target -value)])}]
       (comp-without-sub form)
       some-list
       [:div some-comp]])
    {:subs [:form]}))

(def layout
  (with-meta
    (fn [{:as ctx :keys [sub1]}]
      [:div
       [:h1 "Здарова"]
       [:div sub1]
       page])
    {:subs [:sub1]}))

(defn mount []
  (render/render layout
                 (js/document.querySelector "#app")
                 {:name "Ann"
                  :amount {:value 1}
                  :some "74"}))

(defn ^:after-load re-render [] (mount))

