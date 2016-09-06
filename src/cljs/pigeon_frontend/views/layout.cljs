(ns pigeon-frontend.views.layout
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(defn layout [& body]
  [:div
    [:link {:rel "stylesheet" :type "text/css" :href "assets/bootstrap/css/bootstrap.css"}]
    [:div.navbar.navbar-light.bg-faded
      [:a.navbar-brand {:href "/"} "pigeon-frontend"]
      [:div.pull-xs-right
        [:a {:href "/login"} "Login"]
        [:small.text-muted.m-x-1 "or"]
        [:a.btn.btn-info {:href "/register"} "Sign up"]]
      ]
    [:div.container
      body]])