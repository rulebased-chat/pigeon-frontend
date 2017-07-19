(ns pigeon-frontend.views.layout
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [pigeon-frontend.view-model :refer [app ws-channel navbar-collapsed? errors]]
            [hodgepodge.core :refer [local-storage clear!]]
            [re-frame.core :as re]
            [pigeon-frontend.components :refer [error-container]]))

(defn logout [_]
  (clear! local-storage)
  (println "Closing websocket...")
  (.close @ws-channel)
  (accountant/navigate! "/"))

(defn layout [header lead-text & body]
  (fn [header lead-text & body]
    [:div
      [:link {:rel "stylesheet" :type "text/css" :href "/assets/bootstrap/css/bootstrap.css"}]
      [:div.navbar.navbar-light.bg-faded {:style {:border-bottom "1px solid #d9d9d9" :border-radius 0}}
        [:a.navbar-brand {:href "/"} "pigeon-frontend"]
        (if-let [logged-in? (get-in local-storage [:session])]
          [:div.pull-xs-right
            [:button.btn.btn-info {:on-click logout}
             "Log out"]])]
      [:div.jumbotron
        [:h2 header]
        [:p.lead lead-text]]
      [:div.container-fluid
        (for [error @errors]
          ^{:key error}
          [error-container error])
        body]]))

(defn chat-layout [turn_name & body]
  (fn [turn_name & body]
    [:div {:style {:height "100vh"}}
     [:link {:rel "stylesheet" :type "text/css" :href "/assets/bootstrap/css/bootstrap-flex.css"}]
     [:div.navbar.navbar-light.bg-faded {:style {:border-bottom "1px solid #d9d9d9" :border-radius 0}}
      [:span.navbar-brand.hidden-xs-down turn_name]
      [:div.float-xs-left
       [:a.btn.btn-info.text-white.hidden-sm-up
        {:on-click #(swap! navbar-collapsed? not)}
        "☰"]]
      (if-let [logged-in? (get-in local-storage [:session])]
        [:div.float-xs-right
         [:button.btn.btn-info {:on-click logout}
          "Log out"]]
        [:div.float-xs-right
         [:small.m-r-1 [:a {:href "/login"} "Log in"]]
         [:a.btn.btn-info {:href "/register"} "Sign up"]])]
     [:div.container-fluid {:style {:height "calc(100vh - 55px)"}} body]]))