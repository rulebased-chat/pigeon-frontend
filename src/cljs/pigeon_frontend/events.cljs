(ns pigeon-frontend.events
  (:require [pigeon-frontend.view-model :refer [app]]
            [re-frame.core :as re :refer [debug]]
            [hodgepodge.core :refer [local-storage clear!]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [pigeon-frontend.views.rooms-page :refer [rooms-page]]
            [accountant.core :as accountant]
            [ajax.core :refer [GET POST PUT DELETE]]
            [pigeon-frontend.context :refer [get-context-path]]))

(re/reg-event-db
  :initialize
  (fn [_ _] @app))

;; login page

(re/reg-event-db
  [:fields :login-page :username]
  (fn [db [_ value]]
    (assoc-in db [:fields :login-page :username] value)))

(re/reg-event-db
  [:fields :login-page :password]
  (fn [db [_ value]]
    (assoc-in db [:fields :login-page :password] value)))

;; register page

(re/reg-event-db
  [:fields :register-page :username]
  (fn [db [_ value]]
    (assoc-in db [:fields :register-page :username] value)))

(re/reg-event-db
  [:fields :register-page :password]
  (fn [db [_ value]]
    (assoc-in db [:fields :register-page :password] value)))

(re/reg-event-db
  [:fields :register-page :full-name]
  (fn [db [_ value]]
    (assoc-in db [:fields :register-page :full-name] value)))

;; handlers

(re/reg-event-db
  [:login :success-handler]
  (fn [db [_ value]]
    (let [response value]
      ;; todo: would probably be better if stored in a browser cookie with HttpOnly enabled
      (re/dispatch [:login (:session response)])
      (assoc! local-storage :session (:session response))
      ;; todo: these should really be added through add-watch
      (session/put! :current-page #'rooms-page)
      (accountant/navigate! "/rooms"))))

(re/reg-event-db
  [:login :error-handler]
  (fn [db [_ value]]
    (let [{:keys [status status-text] :as response} value]
      (re/dispatch [:add-error response])
      (.log js/console "Bad response" (str response)))))

;; rooms page

(re/reg-event-db
  [:attempt-login]
  (fn [db [_ value]]
    (POST (get-context-path "/api/v0/session")
      {:params {:username @(re/subscribe [[:fields :login-page :username]])
      :password @(re/subscribe [[:fields :login-page :password]])}
      :handler #(re/dispatch [[:login :success-handler] %1])
      :error-handler #(re/dispatch [[:login :error-handler] %1])
      :response-format :json
      :keywords? true})))

(re/reg-event-db
  [:data :rooms]
  (fn [db [_ value]]
    (assoc-in db [:data :rooms] value)))

;; room create page

(re/reg-event-db
  [:fields :room-create-page :name]
  (fn [db [_ value]]
    (assoc-in db [:fields :room-create-page :name] value)))

;; session

(re/reg-event-db
  :login
  (fn [db [_ session]]
    (assoc-in db [:session] session)))

(re/reg-event-db
  :logout
  (fn [db _]
    (assoc-in db [:session] nil)))

;; errors

(re/reg-event-db
  :add-error
  (fn [db [_ error]]
    (update-in db [:errors] conj error)))

(re/reg-event-db
  :remove-error
  (fn [db [_ error]]
    (update-in db [:errors] disj error)))