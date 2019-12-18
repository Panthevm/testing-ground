(ns ui.style
  (:require [garden.core       :as garden]
            [garden.stylesheet :as stylesheet]
            [re-frame.core  :as rf]))

(def template
  {:color {:body "#e4e4e4"}})

(rf/reg-sub
 ::expands
 (fn [db [_ key]]
   (get-in db [:open key])))

(rf/reg-event-db
 ::expands
 (fn [db [_ key]]
   (update-in db [:open key] not)))

(def app
  [:style
   (garden/css
    (let [{:keys [color]} template]
      (list
       (stylesheet/at-media {:min-width "576px"}  [:.container {:max-width "540px"}])
       (stylesheet/at-media {:min-width "768px"}  [:.container {:max-width "720px"}])
       (stylesheet/at-media {:min-width "992px"}  [:.container {:max-width "960px"}])
       (stylesheet/at-media {:min-width "1200px"} [:.container {:max-width "1140px"}])
       [:html {:box-sizing "border-box"}]
       [:*
        [:&:before {:box-sizing "inherit"}]
        [:&:after {:box-sizing "inherit"}]]
       [:body {:background-color (:body color)}]
       [:.container {:width        "100%"
                     :margin-right "auto"
                     :margin-left  "auto"}]
       [:.row {:display   "flex"
               :flex-wrap "wrap"}]
       [:.col {:flex-basis "0"
               :flex-grow  "1"
               :border     "1px solid red"
               :max-width  "100%"}]

       [:.block {:display "block"}]


       [:.nav-expand {:width "14rem"}]
       [:nav {:position   "absolute"
              :top        "0"
              :left       "0"
              :width      "3.5rem"
              :height     "100%"
              :background "#2680F3"
              :transition "all 300ms ease"
              :overflow   "hidden"
              :z-index    "1"
              :box-shadow "0px 0px 10px #333"}

        [:button {:float "right"
                  :background "inherit"}]
        ])))])
