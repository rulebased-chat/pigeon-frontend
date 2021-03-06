(ns pigeon-frontend.views.login-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.context :refer [get-context-path
                                               get-ws-context-path]]
              [pigeon-frontend.components :refer [users-to-new-messages]]
              [pigeon-frontend.view-model :refer [ws-channel errors]]
              [hodgepodge.core :refer [local-storage clear!]]
              [re-frame.core :as re]
              [cognitect.transit :as transit]
              [cljs.pprint :refer [pprint]]
              [pigeon-frontend.websocket :as websocket]))

(def app (reagent/atom {:username ""
                        :password ""}))

;; fns & login-page

(defn login-successful [response]
  (assoc! local-storage :session (:session response))
  (websocket/start-chsk!)
  (swap! app assoc :username "")
  (swap! app assoc :password "")
  ;; todo proper address
  (accountant/navigate! "/"))

(defn login-user [response]
  (.preventDefault response)
  (POST (get-context-path "/api/v0/session")
    {:params {:username (get-in @app [:username])
              :password (get-in @app [:password])}
     :handler #(login-successful %1)
     :error-handler #(error-handler %1)
     :response-format :json
     :keywords? true}))

(defn login-page []
  [layout/layout "Log in"
                 "Enter your username and password to sign in"
    [:div.row
      [:div.col-sm-12
        [:form {:method "POST"}
          [:div.input-group.mb-1 [:span.input-group-addon "Username"]
                            [:input.form-control {:name "username"
                                                  :value (get-in @app [:username])
                                                  :on-change #(swap! app assoc :username (-> % .-target .-value))}]]
          [:div.input-group.mb-1 [:span.input-group-addon "Password"]
                            [:input.form-control {:name "password"
                                                  :type "password"
                                                  :value (get-in @app [:password])
                                                  :on-change #(swap! app assoc :password (-> % .-target .-value))}]]
          [:p [:button.btn.btn-default.btn-outline-primary {:on-click login-user} "Log in"]]]]]])