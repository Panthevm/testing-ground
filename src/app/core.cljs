(ns ^:figwheel-hooks app.core
  (:require [app.render :as render]
            [app.state-handler :as ne-rf]))

(def some-comp
  (with-meta (fn [{:keys [some-comp-sub]}]
               [:div (str "123" some-comp-sub)])
    {:subs [:some-comp-sub]}))

(defn page [_]
  [:div
   [:h1 "!@#"]
   [:div some-comp]])

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

