(ns pigeon-frontend.views.layout
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [pigeon-frontend.view-model :refer [app ws-channel navbar-collapsed? errors]]
            [hodgepodge.core :refer [local-storage clear!]]
            [re-frame.core :as re]
            [pigeon-frontend.components :refer [error-container logo logo-centered]]
            [pigeon-frontend.context :as context]
            [pigeon-frontend.websocket :refer [chsk-state]]))

(defn logout [_]
  (clear! local-storage)
  (println "Closing websocket...")
  ;;(.close @ws-channel)
  (accountant/navigate! "/"))

(defn layout [header lead-text & body]
  (fn [header lead-text & body]
    [:div
      [:link {:rel "stylesheet" :type "text/css" :href "/assets/bootstrap/css/bootstrap.min.css"}]
      [:div.navbar.navbar-light.bg-faded {:style {:border-bottom "1px solid #d9d9d9" :border-radius 0}}
        [:span.navbar-brand
          [:span.mr-1 [logo]]
          (context/get-title)]
        (if-let [logged-in? (get-in local-storage [:session])]
          [:div.pull-xs-right
            [:button.btn.btn-info.btn-outline-primary {:on-click logout}
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
     [:link {:rel "stylesheet" :type "text/css" :href "/assets/bootstrap/css/bootstrap-flex.min.css"}]
     [:div.navbar.navbar-light.bg-faded {:style {:border-bottom "1px solid #d9d9d9" :border-radius 0}}
      [:span.navbar-brand.hidden-xs-down turn_name]
      [logo-centered]
      [:div.float-xs-left
       [:a.btn.btn-info.text-white.hidden-sm-up
        {:on-click #(swap! navbar-collapsed? not)}
        "☰"]]
      ;; todo: parametrize @ws-channel so that updates immediately (and not when something else changes, ie. textarea)
      (when (false? (:open? @chsk-state))
        [:span.navbar-brand.ml-1.text-danger "Websocket closed"])
      (if-let [logged-in? (get-in local-storage [:session])]
        [:div.float-xs-right
         [:button.btn.btn-info.btn-outline-primary {:on-click logout}
          "Log out"]]
        [:div.float-xs-right
         [:small.m-r-1 [:a {:href "/login"} "Log in"]]])]
     [:div.container-fluid {:style {:height "calc(100vh - 55px)"}} body]]))