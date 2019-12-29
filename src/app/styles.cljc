(ns app.styles
  (:require [garden.core :as garden]))

(defn styles [css]
  [:style (garden/css css)])

(def style
  (app.styles/styles [[:body {:font-family "Arial"
                              :color "#444"}
                       :background-image "url('https://avatars.mds.yandex.net/get-zen_doc/1641049/pub_5d2da9dc520a9b00ad693277_5d2dac00a98a2a00ac385569/scale_1200')"]
                      [:h1 {:font-size "48px"}]
                      [:.content {:padding "1rem"}]
                      [:button {:background-color "#fff"
                                :cursor "pointer"
                                :height "32px"
                                :padding "0 15px"
                                :margin "10px 12px 10px 0"
                                :border-radius "4px"
                                :border "2px solid #999"
                                }]
                      [:button:hover {:color "#e43"
                                      :border-color "#4bf"}]
                      [:.some-comp {:padding "10px 10px 0 0"
                                    }]

                      ]))
