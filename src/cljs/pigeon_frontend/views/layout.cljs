(ns pigeon-frontend.views.layout
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(defn layout [& body]
  [:div.container
    [:link {:rel "stylesheet" :type "text/css" :href "assets/bootstrap/css/bootstrap.css"}]
    body])