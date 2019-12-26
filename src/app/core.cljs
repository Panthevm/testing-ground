(ns ^:figwheel-hooks app.core
  (:require [cljs.spec.alpha :as spec]
            [goog.dom :as dom]))

(defonce vdom (atom nil))

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
   :content     any?))

(defmulti compile-hiccup first)

(defmethod compile-hiccup :content [[_ value]]
  (dom/createTextNode value))

(defmethod compile-hiccup :element [[_ {:keys [tag attr children]}]]
  (apply
   dom/createDom
   (name tag)
   (clj->js attr)
   (map compile-hiccup children)))

(defn changed? [[_ old] [_ new]]
  (and
   (= (:tag old) (:tag new))
   (= (:content old) (:content old))))

(defn childNodes-idx [parent key]
  (aget (.. parent -childNodes) (or key 0)))

(defn hiccup->node [hiccup]
  (->> hiccup
       compile-hiccup))

(defn appendChild [parent child]
  (.appendChild parent
                (hiccup->node child)))

(defn removeChild [parent key]
  (.removeChild parent
                (childNodes-idx parent key)))

(defn replaceChild [parent new key]
  (.replaceChild parent
                 (hiccup->node new)
                 (childNodes-idx parent key)))

(defn childrenLength [parent]
  (.. parent -children -length))

(defn update-element [parent old new & [key]]
  (cond
    (not old)                    (appendChild  parent new)
    (not new)                    (removeChild  parent key)
    (changed? old new) (replaceChild parent new key)
    #_(= :element (first new))        #_(let [[_ {new-c :children}] new
                                            [_ {old-c :children}] old]
                                      (map
                                       (fn [idx]
                                         (update-element
                                          (childNodes-idx parent key)
                                          (nth old-c idx)
                                          (nth new-c idx)
                                          idx))
                                       (range (count (or new-c old-c)))))))

(defn render [new parent]
  (let [old @vdom]
    (->> new
         (spec/conform :hiccup/form)
         (reset! vdom)
         (update-element parent old))))


(def page
  "1")

(comment
  (->> page (spec/conform :hiccup/form)))

(defn mount []
  (js/setInterval #(render (rand) (js/document.querySelector "#app"))
                  0))











(defn ^:after-load re-render [] (mount))
