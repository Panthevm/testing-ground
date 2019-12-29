(ns app.dom
  (:require [app.hiccup :as hiccup]
            [goog.dom   :as dom]))

(defmulti node first)

(defmethod node :content [[_ value]]
  (when value
    (dom/createTextNode value)))

(defmethod node :element [[_ {:keys [tag attr children]}]]
  (apply dom/createDom
         (name tag)
         (clj->js attr)
         (map node children)))

(defn nodes-idx [parent key]
  (aget (.-childNodes parent) (or key 0)))

(defn append-child
  ([parent child]
   (dom/appendChild parent
                    (node child))))

(defn remove-child [parent key]
  (dom/removeNode (nodes-idx parent key)))

(defn replace-child [parent new key]
  (dom/replaceNode (node new)
                   (nodes-idx parent key)))
