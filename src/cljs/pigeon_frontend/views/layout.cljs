(ns pigeon-frontend.views.layout
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [pigeon-frontend.view-model :refer [app]]
            [hodgepodge.core :refer [local-storage clear!]]
            [re-frame.core :as re]))

(defn logout [_]
  (re/dispatch [:logout])
  (clear! local-storage)
  (accountant/navigate! "/"))

(defn layout [header lead-text & body]
  (fn [header lead-text & body]
    [:div
      [:link {:rel "stylesheet" :type "text/css" :href "assets/bootstrap/css/bootstrap.css"}]
      [:div.navbar.navbar-light.bg-faded
        [:a.navbar-brand {:href "/"} "pigeon-frontend"]
        (if-let [logged-in? @(re/subscribe [:session-token])]
          [:div.pull-xs-right
            [:button.btn.btn-info {:on-click logout} "Log out"]]
          [:div.pull-xs-right
            [:small.m-r-1 [:a {:href "/login"} "Log in"]]
            [:a.btn.btn-info {:href "/register"} "Sign up"]])]
      [:div.jumbotron
        [:h2 header]
        [:p.lead lead-text]]
      [:div.container-fluid
        (for [error @(re/subscribe [:errors])]
          ^{:key error}
          [:div.alert.alert-danger.alert-dismissible.fade.in {:role "alert"}
            [:strong (:status-text error)] (str " " (get-in error [:response :title]))
            [:button.close {:type "button"
                            :data-dismiss "alert"
                            :aria-label "Close"
                            :on-click #(re/dispatch [:remove-error error])} "x"]])
        body]]))