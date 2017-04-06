(ns pigeon-frontend.subscriptions
  (:require [re-frame.core :as re :refer [debug]]))

;; login page

(re/reg-sub
  [:fields :login-page :username]
  (fn [db _]
    (get-in db [:fields :login-page :username])))

(re/reg-sub
  [:fields :login-page :password]
  (fn [db _]
    (get-in db [:fields :login-page :password])))

;; register page

(re/reg-sub
  [:fields :register-page :username]
  (fn [db _]
    (get-in db [:fields :register-page :username])))

(re/reg-sub
  [:fields :register-page :password]
  (fn [db _]
    (get-in db [:fields :register-page :password])))

(re/reg-sub
  [:fields :register-page :full-name]
  (fn [db _]
    (get-in db [:fields :register-page :full-name])))

;; rooms page

(re/reg-sub
  [:data :rooms]
  (fn [db _]
    (get-in db [:data :rooms])))

;; room create page

(re/reg-sub
  [:fields :room-create-page :name]
  (fn [db _]
    (get-in db [:fields :room-create-page :name])))

;; room page

(defn get-navbar-mobile-collapsed [db]
  (get-in db [:navbar-mobile :collapsed]))

(re/reg-sub
  [:navbar-mobile :collapsed]
  (fn [db _]
    (get-navbar-mobile-collapsed db)))

(re/reg-sub
  [:navbar-mobile :display]
  (fn [db _]
    (if-let [collapsed? (get-navbar-mobile-collapsed db)]
      "none"
      "block")))

(re/reg-sub
  [:data :room :participants]
  (fn [db _]
    (get-in db [:data :room :participants])))

(re/reg-sub
  [:data :room :sender :id]
  (fn [db _]
    (get-in db [:data :room :sender :id])))

(defn get-chat-input-value [db]
  (get-in db [:chat-input :value]))

(re/reg-sub
  [:chat-input :value]
  (fn [db _]
    (get-chat-input-value db)))

(re/reg-sub
  [:chat-input :rows]
  (fn [db _]
  (let [value (get-chat-input-value db)
        rows (count (re-seq #"\n" value))
        rowcap 6
        line-height-in-pixels 18
        rows-or-rowcap (if (< rows rowcap) rows rowcap)]
    (* rows-or-rowcap line-height-in-pixels))))

;; chat page

(re/reg-sub
  [:fields :chat-page :room_id]
  (fn [db _]
    (get-in db [:fields :chat-page :room_id])))

(re/reg-sub [:fields :chat-page :sender]
  (fn [db _]
    (get-in db [:fields :chat-page :sender])))

(re/reg-sub [:fields :chat-page :recipient]
  (fn [db _]
    (get-in db [:fields :chat-page :recipient])))

(re/reg-sub [:fields :chat-page :message]
  (fn [db _]
    (get-chat-input-value db)))

;; session

(re/reg-sub
  :session
  (fn [db _]
    (get-in db [:session])))

(re/reg-sub
  :session-token
  (fn [db _]
    (get-in db [:session :token])))

(re/reg-sub
  :session-username
  (fn [db _]
    (get-in db [:session :username])))

;; errors

(re/reg-sub
  :errors
  (fn [db _]
    (get-in db [:errors])))