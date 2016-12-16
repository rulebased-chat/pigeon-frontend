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

(defn login-user [response]
  (POST (get-context-path "/api/v0/session")
    {:params {:username (get-in @app [:fields :register-page :username])
              :password (get-in @app [:fields :register-page :password])}
     :handler login-successful
     :error-handler error-handler
     :response-format :json
     :keywords? true}))

(defn register-user [event]
  (.preventDefault event)
  (let [response (PUT (get-context-path "/api/v0/user")
        {:params {:username (get-in @app [:fields :register-page :username])
                  :password (get-in @app [:fields :register-page :password])
                  :full_name (get-in @app [:fields :register-page :full-name])}
         :handler login-user
         :error-handler error-handler})]))

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
          [:p [:input {:name "full_name" 
                       :placeholder "full_name" 
                       :value @(re/subscribe [[:fields :register-page :full-name]])
                       :on-change #(re/dispatch [[:fields :register-page :full-name] (-> % .-target .-value)])}]]
          [:p [:button.btn.btn-default {:type "submit"} "Submit"]]]]]])