(ns ^:figwheel-hooks app.core
  (:require [cljs.spec.alpha :as spec]
            [goog.dom :as dom]))

(spec/def :hiccup/element
  (spec/and
   vector?
   (spec/cat
    :tag      keyword?
    :attr     (spec/? map?)
    :children (spec/* :hiccup/form))))

(spec/def :hiccup/form
  (spec/or
   :element :hiccup/element
   :any     any?))

(defmulti compile-hiccup first)

(defmethod compile-hiccup :any [[_ value]]
  value)

(defmethod compile-hiccup :element [[_ {:keys [tag attr children]}]]
  [tag attr children]
  (apply
   dom/createDom
   (name tag)
   (clj->js attr)
   (map compile-hiccup children)))

(defn render [element node]
  (->> element
       (spec/conform :hiccup/form)
       compile-hiccup
       (dom/append node)))

(def page
  [:h1 {:title "title"} "Title"])

(defn mount []
  (render page (js/document.querySelector "#app")))










(defn ^:after-load re-render [] (mount))
