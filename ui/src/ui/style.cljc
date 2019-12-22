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

(rf/reg-sub
 ::expands
 (fn [db [_ key]]
   (get-in db [:open key])))

(rf/reg-event-db
 ::expands
 (fn [db [_ key]]
   (update-in db [:open key] not)))

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
          [:html {:box-sizing "border-box"}]
          [:*
           [:&:before {:box-sizing "inherit"}]
           [:&:after {:box-sizing "inherit"}]]
          [:&:focus
           {:outline "none"}]
          [:body {:background-color (theme (:body color))
                  :font-family "GothamPro"
                  :transition       "all 300ms ease"}]
          [:.container {:width        "100%"
                        :margin-right "auto"
                        :transition "all 300ms ease"
                        :margin-left  "auto"}]
          [:.row {:display   "flex"
                  :flex-wrap "wrap"}]
          [:.col {:flex-basis "0"
                  :flex-grow  "1"
                  :border     "1px solid red"
                  :max-width  "100%"}]
          [:.block {:display "block"}]
          [:.fixed {:position "fixed"}]
          [:.shadow {:box-shadow "0 10px 20px rgba(0,0,0,0.19), 0 6px 6px rgba(0,0,0,0.23)"}]
                                        ;#Typography
          [:a :span :p :h1 :h3 :img
           {:color (theme (:text color))}]
          [:.active {:font-weight "600"}]
                                        ;#Icon

          [:img {:height "18px" :width "18px"}]
          [:.close.icon :.menu-icon
           {:color (theme (:text color))}]

                                        ;#Buttons
          [:button {:border        "none"
                    :border-radius "6px"}]

                                        ;#Navigation
          [:.nav-expand {:left "-250px"}]
          [:.menu-open {:left "0"
                        :background-color (theme (:main color))
                        :top "10px"
                        :border-radius "0px 6px 6px 0px"
                        :padding "15px 10px 15px 10px"}]
          [:.menu-close {:margin "10px 5px 0 0"}]
          [:.body-wrapper {:padding-left "calc(6% + 250px)"
                           :transition "all 300ms ease"}]
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
                     :background "inherit"}]
           ]))]))))
