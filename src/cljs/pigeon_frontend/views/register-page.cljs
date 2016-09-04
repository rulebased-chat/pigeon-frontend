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
  (swap! app assoc-in [:session :token] (:token response)))

(defn login-user [response]
  (POST "http://localhost:3000/api/v0/session"
    {:params {:username (get-in @app [:user :username])
              :password (get-in @app [:user :password])}
     :handler login-successful
     :error-handler error-handler 
     :response-format :json 
     :keywords? true}))

(defn register-user [_]
  (let [response (PUT "http://localhost:3000/api/v0/user" 
        {:params {:username (get-in @app [:user :username])
                  :password (get-in @app [:user :password])
                  :full_name (get-in @app [:user :full-name])}
         :handler login-user
         :error-handler error-handler})]))

(defn register-page []
  [layout/layout
    [:div.row
      [:div.col-sm-12
        [:h2 "Sign up"]
        [:div
          [:p [:input {:name "username" :placeholder "username" 
                       :value (get-in @app [:user :username]) 
                       :on-change #(swap! app assoc-in [:user :username] (-> % .-target .-value))}]]
          [:p [:input {:name "password" :placeholder "password" :type "password" 
                       :value (get-in @app [:user :password]) 
                       :on-change #(swap! app assoc-in [:user :password] (-> % .-target .-value))}]]
          [:p [:input {:name "full_name" :placeholder "full_name" 
                       :value (get-in @app [:user :full-name]) 
                       :on-change #(swap! app assoc-in [:user :full-name] (-> % .-target .-value))}]]
          [:p [:button.btn.btn-default {:type "submit" :on-click register-user} "Submit"]]]]]])