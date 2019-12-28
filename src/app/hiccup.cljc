(ns app.hiccup
  (:require [clojure.spec.alpha :as spec]))

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

(defn page []
  [:div
   [:h1
    "!@#"]])

(defn layout []
  (let [db {:h1   "h1"
            :page "page"}]
    [:div
     [:h1 (:h1 db)]
     #_[page]]))

(conform [layout])
