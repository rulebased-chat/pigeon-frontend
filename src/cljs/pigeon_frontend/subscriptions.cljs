(ns pigeon-frontend.subscriptions
  (:require [re-frame.core :as re]))

(re/reg-sub
  [:fields :login-page :username]
  (fn [db _]
    (get-in db [:fields :login-page :username])))

(re/reg-sub
  [:fields :login-page :password]
  (fn [db _]
    (get-in db [:fields :login-page :password])))

(re/reg-sub
  [:fields :register-page :username]
  (fn [db _]
    (get-in db [:fields :register-page :username])))

(re/reg-sub
  [:fields :register-page :password]
  (fn [db _]
    (get-in db [:fields :register-page :password])))

(re/reg-sub
  [:fields :register-page :password]
  (fn [db _]
    (get-in db [:fields :register-page :password])))

(re/reg-sub
  [:fields :register-page :full-name]
  (fn [db _]
    (get-in db [:fields :register-page :full-name])))

(re/reg-sub
  [:fields :room-create-page :name]
  (fn [db _]
    (get-in db [:fields :room-create-page :name])))