(ns ^:figwheel-hooks app.core
  (:require [app.render :as render]
            [app.state-handler :as state]))

(def some-comp
  (with-meta (fn [{:keys [some-comp-sub]}]
               [:div (str "123" some-comp-sub)])
    {:subs [:some-comp-sub]}))

(def page
  (with-meta
    (fn [{:keys [form] :as ss}]
      [:div
       [:h1 form]
       [:h1 "@#!@#!@"]
       [:button {:onclick #(state/dispatch [:inc-state])} "Inc state"]
       [:button {:onclick #(state/dispatch [:inc-state])} "Inc state"]
       [:button {:onclick #(state/dispatch [:change-some])} "Change some"]
       [:input {:value form
                :onchange #(state/dispatch [:input (.. % -target -value)])}]
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
                 (js/document.querySelector "#app")))

(defn ^:after-load re-render [] (mount))

