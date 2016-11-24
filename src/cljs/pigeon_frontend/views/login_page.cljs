(ns pigeon-frontend.views.login-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.view-model :refer [app]]
              [pigeon-frontend.context :refer [get-context-path]]
              [pigeon-frontend.views.rooms-page :refer [rooms-page]]))

(defn login-successful [response]
  ;; todo: would probably be better if stored in a browser cookie with HttpOnly enabled
  (swap! app merge response)
  (.setItem (.-localStorage js/window) "token"
                                       (get-in response [:session :token]))
  ;; todo: these should really be added through add-watch
  (session/put! :current-page #'rooms-page)
  (accountant/navigate! "/rooms"))

(defn login-user [response]
  (.preventDefault response)
  (POST (get-context-path "/api/v0/session")
    {:params {:username (get-in @app [:fields :login-page :username])
              :password (get-in @app [:fields :login-page :password])}
     :handler login-successful
     :error-handler error-handler
     :response-format :json
     :keywords? true}))

(defn login-page []
  [layout/layout "Log in"
                 "Enter your username and password to sign in"
    [:div.row
      [:div.col-sm-12
        [:form {:on-submit login-user}
          [:p [:input {:name "username" 
                       :placeholder "username" 
                       :value (get-in @app [:fields :login-page :username]) 
                       :on-change #(swap! app assoc-in [:fields :login-page :username] (-> % .-target .-value))}]]
          [:p [:input {:name "password" 
                       :placeholder "password" :type "password" 
                       :value (get-in @app [:fields :login-page :password]) 
                       :on-change #(swap! app assoc-in [:fields :login-page :password] (-> % .-target .-value))}]]
          [:p [:button.btn.btn-default {:type "submit"} "Submit"]]]]]])