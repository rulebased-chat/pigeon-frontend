(ns pigeon-frontend.views.layout
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(defn layout [header lead-text & body]
  [:div
    [:link {:rel "stylesheet" :type "text/css" :href "assets/bootstrap/css/bootstrap.css"}]
    [:div.navbar.navbar-light.bg-faded
      [:a.navbar-brand {:href "/"} "pigeon-frontend"]
      [:div.pull-xs-right
        [:small.m-r-1 [:a {:href "/login"} "Log in"]]
        [:a.btn.btn-info {:href "/register"} "Sign up"]]]
    [:div.jumbotron
      [:h2 header]
      [:p.lead lead-text]]
    [:div.container-fluid 
      body]])