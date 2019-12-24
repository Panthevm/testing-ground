(ns ui.routes
  (:require [ui.home.view     :as home]
            [ui.settings.view :as settings]))

(def routes
  {:.        #'home/index
   :settings {:main #'settings/index}})
