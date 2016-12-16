(ns pigeon-frontend.events
  (:require [pigeon-frontend.view-model :refer [app]]
            [re-frame.core :as re :refer [debug]]))

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

;; rooms page

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