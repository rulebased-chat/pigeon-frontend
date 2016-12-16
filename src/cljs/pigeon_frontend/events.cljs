(ns pigeon-frontend.events
  (:require [pigeon-frontend.view-model :refer [app]]
            [re-frame.core :as re]))

(re/reg-event-db
  :initialize
  (fn [_ _] @app))

(re/reg-event-db
  [:fields :login-page :username]
  (fn [db [key value]]
    (assoc-in db [:fields :login-page :username] value)))

(re/reg-event-db
  [:fields :login-page :password]
  (fn [db [key value]]
    (assoc-in db [:fields :login-page :password] value)))

(re/reg-event-db
  [:fields :register-page :username]
  (fn [db [key value]]
    (assoc-in db [:fields :register-page :username] value)))

(re/reg-event-db
  [:fields :register-page :password]
  (fn [db [key value]]
    (assoc-in db [:fields :register-page :password] value)))

(re/reg-event-db
  [:fields :register-page :full-name]
  (fn [db [key value]]
    (assoc-in db [:fields :register-page :full-name] value)))

(re/reg-event-db
  [:fields :room-create-page :name]
  (fn [db [key value]]
    (assoc-in db [:fields :room-create-page :name] value)))