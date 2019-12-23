(ns ui.routes
  (:require [ui.home.view     :as home]
            [ui.settings.view :as settings]))

(def routes
  ["/"
   [""          {:view   #'home/index
                 :init   (fn [] (prn "home init"))
                 :deinit (fn [] (prn "home deinit"))}]
   ["settings"  {:view #'settings/index
                 :init   (fn [] (prn "settings init"))
                 :deinit (fn [] (prn "settings deinit"))}]])
