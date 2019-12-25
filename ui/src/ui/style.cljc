(ns ui.style
  (:require [garden.core       :as garden]
            [garden.stylesheet :as stylesheet]
            [re-frame.core  :as rf]))

(rf/reg-event-db
 ::dark-theme
 (fn [db]
   (update db :dark-theme not)))

(rf/reg-sub
 ::dark-theme
 (fn [db]
   (get db :dark-theme)))

(def template;   [light]    [dark]
  {:color {:body ["#e4e4e4" "#121212"]
           :main ["#2680F3" "#1f1f1f"]
           :text ["#000"    "#e2e2e2"]}})

(defn app []
  (let [dark-theme (rf/subscribe [::dark-theme])]
    (fn []
      (let [{:keys [color]} template
            theme           (fn [colors]
                              ((if @dark-theme last first) colors))]
       [:style
        (garden/css
         (list
          (stylesheet/at-media {:min-width "576px"}  [:.container {:max-width "540px"}])
          (stylesheet/at-media {:min-width "768px"}  [:.container {:max-width "720px"}])
          (stylesheet/at-media {:min-width "992px"}  [:.container {:max-width "960px"}])
          (stylesheet/at-media {:min-width "1200px"} [:.container {:max-width "1140px"}])
          [:&:focus {:outline "none"}]
          [:body {:background-color (theme (:body color))
                  :font-family      "GothamPro"}]
          [:.container {:width        "100%"
                        :margin-right "auto"
                        :transition   "all 300ms ease"
                        :margin-left  "auto"}]
                                        ;#Aliases
          [:.block {:display "block"}]
                                        ;#Grid
          [:.row {:display   "flex"
                  :flex-wrap "wrap"}]
          [:.col {:flex-basis "0"
                  :flex-grow  "1"
                  :border     "1px solid red"
                  :max-width  "100%"}]
                                        ;#Typography
          [:a :span :p :h1 :h3 :img
           {:color (theme (:text color))}]
          [:.active {:font-weight "600"}]
                                        ;#Icon

          [:.close.icon :.menu-icon
           {:color (theme (:text color))}]
                                        ;#Buttons
          [:button {:border        "none"
                    :border-radius "6px"}]

                                        ;#Navigation
          [:.nav-expand {:left "-250px"}]
          [:.menu-open {:left             "0"
                        :position         "fixed"
                        :background-color (theme (:main color))
                        :top              "10px"
                        :border-radius    "0px 6px 6px 0px"
                        :padding          "15px 10px 15px 10px"}]
          [:.menu-close {:margin "10px 5px 0 0"}]
          [:nav {:position   "fixed"
                 :top        "0"
                 :left       "0"
                 :width      "250px"
                 :height     "100%"
                 :background (theme (:main color))
                 :transition "all 300ms ease"
                 :overflow   "hidden"
                 :z-index    "1"
                 :box-shadow "0px 0px 3px #333"}
           [:.link {:margin          "20px"
                    :text-decoration "none"}]
           [:button {:float      "right"
                     :background "inherit"}]]))]))))
