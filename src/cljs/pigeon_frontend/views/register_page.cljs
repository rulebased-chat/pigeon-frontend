(ns pigeon-frontend.views.register-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.view-model :refer [app]]
              [pigeon-frontend.context :refer [get-context-path]]
              [pigeon-frontend.views.login-page :refer [login-successful]]
              [pigeon-frontend.views.rooms-page :refer [rooms-page]]
              [re-frame.core :as re]))

(defn register-user [event]
  (.preventDefault event)
  (re/dispatch [[:register-user]]))

(defn register-page []
  [layout/layout "Sign up"
                 "Pigeon is a rule-based messaging service for simulating war communications in multiplayer wargaming sessions"
    [:div.row
      [:div.col-sm-12
        [:form {:on-submit register-user}
          [:p [:input {:name "username" 
                       :placeholder "username" 
                       :value @(re/subscribe [[:fields :register-page :username]])
                       :on-change #(re/dispatch [[:fields :register-page :username] (-> % .-target .-value)])}]]
          [:p [:input {:name "password" 
                       :placeholder "password" :type "password" 
                       :value @(re/subscribe [[:fields :register-page :password]])
                       :on-change #(re/dispatch [[:fields :register-page :password] (-> % .-target .-value)])}]]
          [:p [:input {:name "name"
                       :placeholder "name"
                       :value @(re/subscribe [[:fields :register-page :full-name]])
                       :on-change #(re/dispatch [[:fields :register-page :full-name] (-> % .-target .-value)])}]]
          [:p [:button.btn.btn-default {:type "submit"} "Submit"]]]]]])