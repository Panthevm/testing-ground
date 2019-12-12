(ns ^:figwheel-no-load ui.dev
  (:require [ui.core       :refer [mount]]
            [re-frisk.core :refer [enable-re-frisk!]]
            [devtools.core :refer [install!]]))

(install!)
(enable-re-frisk! {:width "400px" :height "500px"})
(mount)
