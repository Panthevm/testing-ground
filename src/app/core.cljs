(ns ^:figwheel-hooks app.core
  (:require [app.render :as render]
            [app.state-handler :as state]))

(def some-comp
  (with-meta (fn [{:keys [some-comp-sub]}]
               [:div (str "123" some-comp-sub)])
    {:subs [:some-comp-sub]}))

(def page
  (with-meta
    (fn [_]
      [:div
       [:button {:onclick #(state/dispatch [:inc-state])} "123"]
       [:h1 "!@#"]
       [:div some-comp]])
    {:subs []}))

(def layout
  (with-meta
    (fn [{:as ctx :keys [sub1]}]
      [:div
       [:h1 (:h1 ctx)]
       [:div sub1]
       page])
    {:subs [:sub1]}))

(defn mount []
  (render/render layout
                 (js/document.querySelector "#app")))

(defn ^:after-load re-render [] (mount))

