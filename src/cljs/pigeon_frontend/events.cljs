(ns pigeon-frontend.events
  (:require [pigeon-frontend.view-model :refer [app]]
            [re-frame.core :as re]))

(re/reg-event-db
  :initialize
  (fn [_ _] @app))

(re/reg-event-db
  :login-page-username
  (fn [db [key value]]
    (assoc-in db [:fields :login-page :username] value)))

(re/reg-event-db
  :login-page-password
  (fn [db [key value]]
    (assoc-in db [:fields :login-page :password] value)))