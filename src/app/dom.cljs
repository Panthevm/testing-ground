(ns app.dom
  (:require [app.hiccup :as hiccup]
            [goog.dom   :as dom]))

(defmulti node first)

(defmethod node :content [[_ value]]
  (dom/createTextNode value))

(defmethod node :element [[_ {:keys [tag attr children]}]]
  (apply dom/createDom
         (name tag)
         (clj->js attr)
         (map node children)))

(defn nodes-idx [parent key]
  (aget (.. parent -childNodes) (or key 0)))

(defn append-child [parent child]
  (.appendChild parent
                (node child)))

(defn remove-child [parent key]
  (.removeChild parent
                (nodes-idx parent key)))

(defn replace-child [parent new key]
  (.replaceChild parent
                 (node new)
                 (nodes-idx parent key)))
