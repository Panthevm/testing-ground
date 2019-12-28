(ns app.hiccup
  (:require [clojure.spec.alpha :as spec]
            [app.state-handler :as state]))

(spec/def :hiccup/element
  (spec/and
   vector?
   (spec/cat
    :tag      keyword?
    :attr     (spec/? map?)
    :children (spec/* :hiccup/form))))

(spec/def :hiccup/form
  (spec/or
   :element   :hiccup/element
   :content   any?))

(defn conform
  [hiccup]
  (spec/conform :hiccup/form hiccup))

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

(def ctx (merge (state/get-init-subs)
                {:h1   "h1"
                 :page "page"}))

(clojure.pprint/pprint (clojure.walk/prewalk #(if (fn? %) (% ctx) %) [layout]))

