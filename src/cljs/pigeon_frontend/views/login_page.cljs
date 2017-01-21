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
              [pigeon-frontend.views.rooms-page :refer [rooms-page]]
              [hodgepodge.core :refer [local-storage clear!]]
              [re-frame.core :as re]))

(defn login-successful [response]
  ;; todo: would probably be better if stored in a browser cookie with HttpOnly enabled
  (re/dispatch [:login (:session response)])
  (assoc! local-storage :session (:session response))
  (accountant/navigate! "/rooms"))

(defn login-user [response]
  (.preventDefault response)
  (re/dispatch [[:attempt-login]]))

(defn login-page []
  [layout/layout "Log in"
                 "Enter your username and password to sign in"
    [:div.row
      [:div.col-sm-12
        [:form {:on-submit login-user}
          [:p [:input {:name "username"
                       :placeholder "username"
                       :value @(re/subscribe [[:fields :login-page :username]])
                       :on-change #(re/dispatch [[:fields :login-page :username] (-> % .-target .-value)])}]]
          [:p [:input {:name "password"
                       :placeholder "password" :type "password"
                       :value @(re/subscribe [[:fields :login-page :password]])
                       :on-change #(re/dispatch [[:fields :login-page :password] (-> % .-target .-value)])}]]
          [:p [:button.btn.btn-default {:type "submit"} "Submit"]]]]]])