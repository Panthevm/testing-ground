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
  (if (and (map? old) (map? new))
    (not= (:tag old) (:tag new))
    (not= old new)))

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
    (nil? old)               (appendChild  parent new)
    (nil? new)               (removeChild  parent key)
    (changed? old new)       (replaceChild parent new key)
    (= :element (first new)) (let [[_ {new-c :children}] new
                                   [_ {old-c :children}] old]
                               (doall
                                (map-indexed
                                 (fn [idx [o n]]
                                   (prn "@@@" idx)
                                   (update-element (childNodes-idx parent key) o n idx))
                                 (map vector new-c (concat old-c (repeat nil))))))))

(defn render [new parent]
  (let [old @vdom]
    (->> new
         (spec/conform :hiccup/form)
         (reset! vdom)
         (update-element parent old))))

(def page
  [:div
   [:h1 "DOM"]
   [:div
    [:h1 {:label "label"} "1"]]])

(comment
  (->> page (spec/conform :hiccup/form)))

(defn mount []
  (js/setInterval #(render [:div
                            [:h1 (if (zero? (rand-int 2))
                                   "Чувашский"
                                   "DOM")]
                            [:div
                             [:div
                              [:h1 {:label "label"} (rand)]
                              ]]
                            [:div
                             [:h1 {:label "label"} (rand)]
                             [:h1 {:label "label"} "1"]]] (js/document.querySelector "#app"))
                  0))











(defn ^:after-load re-render [] (mount))
