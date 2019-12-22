(ns ui.routes
  (:require [ui.home.view     :as home]
            [ui.settings.view :as settings]))

(def routes
  ["/"
   [""          {:view #'home/index}]
   ["settings"  {:view #'settings/index}]])


