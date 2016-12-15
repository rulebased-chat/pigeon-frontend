(ns pigeon-frontend.subscriptions
  (:require [re-frame.core :as re]))

(re/reg-sub
  :login-page-username
  (fn [db _]
    (get-in db [:fields :login-page :username])))

(re/reg-sub
  :login-page-password
  (fn [db _]
    (get-in db [:fields :login-page :password])))