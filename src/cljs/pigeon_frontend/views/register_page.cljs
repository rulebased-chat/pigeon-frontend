(ns pigeon-frontend.views.register-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.view-model :refer [app]]))

(defn login-successful [response]
  (swap! app assoc :session response))

(defn login-user [response]
  (POST "http://localhost:3000/api/v0/session"
    {:params {:username (get-in @app [:fields :register-page :username])
              :password (get-in @app [:fields :register-page :password])}
     :handler login-successful
     :error-handler error-handler 
     :response-format :json 
     :keywords? true}))

(defn register-user [_]
  (let [response (PUT "http://localhost:3000/api/v0/user" 
        {:params {:username (get-in @app [:fields :register-page :username])
                  :password (get-in @app [:fields :register-page :password])
                  :full_name (get-in @app [:fields :register-page :full-name])}
         :handler login-user
         :error-handler error-handler})]))

(defn register-page []
  [layout/layout
    [:div.row
      [:div.col-sm-12
        [:h2 "Sign up"]
        [:div
          [:p [:input {:name "username" 
                       :placeholder "username" 
                       :value (get-in @app [:fields :register-page :username]) 
                       :on-change #(swap! app assoc-in [:fields :register-page :username] (-> % .-target .-value))}]]
          [:p [:input {:name "password" 
                       :placeholder "password" :type "password" 
                       :value (get-in @app [:fields :register-page :password]) 
                       :on-change #(swap! app assoc-in [:fields :register-page :password] (-> % .-target .-value))}]]
          [:p [:input {:name "full_name" 
                       :placeholder "full_name" 
                       :value (get-in @app [:fields :register-page :full-name]) 
                       :on-change #(swap! app assoc-in [:fields :register-page :full-name] (-> % .-target .-value))}]]
          [:p [:button.btn.btn-default {:type "submit" :on-click register-user} "Submit"]]]]]])