(ns pigeon-frontend.views.layout
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [pigeon-frontend.view-model :refer [app]]
            [hodgepodge.core :refer [local-storage clear!]]
            [re-frame.core :as re]))

;; todo:
(defn logout [_]
  (clear! local-storage)
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
        (for [error '() ;; todo: @(re/subscribe [:errors])
              ]
          ^{:key error}
          [:div.alert.alert-danger.alert-dismissible.fade.in {:role "alert"}
            [:strong (:status-text error)] (str " " (get-in error [:response :title]))
            [:button.close {:type "button"
                            :data-dismiss "alert"
                            :aria-label "Close"
                            ;; todo: :on-click #(re/dispatch [:remove-error error])
                            } "x"]])
        body]]))

(defn chat-layout [turn_name & body]
  (fn [turn_name & body]
    [:div {:style {:height "100vh"}}
     [:link {:rel "stylesheet" :type "text/css" :href "/assets/bootstrap/css/bootstrap-flex.css"}]
     [:div.navbar.navbar-light.bg-faded {:style {:border-bottom "1px solid #d9d9d9" :border-radius 0}}
      [:span.navbar-brand.hidden-xs-down turn_name]
      [:div.float-xs-left
       [:a.btn.btn-info.text-white.hidden-sm-up
        {:on-click #(re/dispatch [[:navbar-mobile :collapsed] @(re/subscribe [[:navbar-mobile :collapsed]])])}
        "☰"]]
      (if-let [logged-in? (get-in local-storage [:session])]
        [:div.float-xs-right
         [:button.btn.btn-info {:on-click logout}
          "Log out"]]
        [:div.float-xs-right
         [:small.m-r-1 [:a {:href "/login"} "Log in"]]
         [:a.btn.btn-info {:href "/register"} "Sign up"]])]
     [:div.container-fluid {:style {:height "calc(100vh - 55px)"}} body]]))