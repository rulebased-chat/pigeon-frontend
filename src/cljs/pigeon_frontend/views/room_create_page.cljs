(ns pigeon-frontend.views.room-create-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.view-model :refer [app]]
              [pigeon-frontend.context :refer [get-context-path]]
              [pigeon-frontend.views.login-page :refer [login-page]]))

(defn redirect-upon-success []
  (session/put! :current-page #'login-page)
  (accountant/navigate! "/rooms"))

(defn create-room [_]
  (let [response (POST (get-context-path "/api/v0/room")
        {:params {:name (get-in @app [:fields :room-create-page :name])}
         :headers {:authorization (str "Bearer " (get-in @app [:session :token]))}
         :handler redirect-upon-success
         :error-handler error-handler})]))

(defn room-create-page []
  [layout/layout "New room"
                 "A room consists of many people and groups"
    [:div.row
      [:div.col-sm-12
        [:div
          [:p [:input {:name "name"
                       :placeholder "name"
                       :value (get-in @app [:fields :room-create-page :name])
                       :on-change #(swap! app assoc-in [:fields :room-create-page :name] (-> % .-target .-value))}]]
          [:p [:button.btn.btn-default {:type "submit" :on-click create-room} "Submit"]]]]]])