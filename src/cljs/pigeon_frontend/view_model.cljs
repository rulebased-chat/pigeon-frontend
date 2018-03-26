(ns pigeon-frontend.view-model
    (:require [reagent.core :as reagent :refer [atom]]))

(defonce ws-channel (reagent/atom nil))
(defonce navbar-collapsed? (atom true))
(defonce errors (reagent/atom #{}))
(defonce app (reagent/atom {:navbar-mobile {:collapsed true}}))