(ns pigeon-frontend.events
  (:require [pigeon-frontend.view-model :refer [app]]
            [re-frame.core :as re :refer [debug]]
            [hodgepodge.core :refer [local-storage clear!]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [ajax.core :refer [GET POST PUT DELETE json-request-format json-response-format]]
            [pigeon-frontend.context :refer [get-context-path]]
            [pigeon-frontend.views.login-page :refer [login-page]]))

(re/reg-event-db
  :initialize
  (fn [_ _] @app))

;; fns

(defn set-chat-input-value! [db value]
  (assoc-in db [:chat-input :value] value))

;; login page

(re/reg-event-db
  [:fields :login-page :username]
  (fn [db [_ value]]
    (assoc-in db [:fields :login-page :username] value)))

(re/reg-event-db
  [:fields :login-page :password]
  (fn [db [_ value]]
    (assoc-in db [:fields :login-page :password] value)))

;; handlers

(re/reg-event-db
  [:error-handler]
  (fn [db [_ value]]
    (let [{:keys [status status-text] :as response} value]
      (re/dispatch [:add-error response])
      (.log js/console "Bad response" (str response)))
    db))

(re/reg-event-db
  [:login :success-handler]
  (fn [db [_ value]]
    (let [response value]
      ;; todo: would probably be better if stored in a browser cookie with HttpOnly enabled
      (re/dispatch [:login (:session response)])
      (assoc! local-storage :session (:session response))
      (accountant/navigate! "/rooms"))
    db))

(re/reg-event-db
  [:login :session-handler]
  (fn [db [_ value]]
    (let [response value]
      ;; todo: would probably be better if stored in a browser cookie with HttpOnly enabled
      (assoc! local-storage :session (:session response))
      (accountant/navigate! "/rooms"))
    db))

(re/reg-event-db
  [:register-user :success-handler]
  (fn [db [_ response]]
    (POST (get-context-path "/api/v0/session")
      {:params {:username @(re/subscribe [[:fields :register-page :username]])
                :password @(re/subscribe [[:fields :register-page :password]])}
      :handler #(re/dispatch [[:login :session-handler] %1])
      :error-handler #(re/dispatch [[:error-handler] %1])
       :response-format :json
       :keywords? true})
    db))

(re/reg-event-db
  [:send-message :success]
  (fn [db [_ value]]
    (set-chat-input-value! db "")))

;; register page

(re/reg-event-db
  [:register-user]
  (fn [db [_ value]]
    (let [response value]
      (PUT (get-context-path "/api/v0/user")
        {:params {:username @(re/subscribe [[:fields :register-page :username]])
                  :password @(re/subscribe [[:fields :register-page :password]])
                  :name @(re/subscribe [[:fields :register-page :full-name]])}
         :handler #(re/dispatch [[:register-user :success-handler] %1])
         :error-handler #(re/dispatch [[:error-handler] %1])}))
    db))

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
  [:attempt-login]
  (fn [db [_ value]]
    (POST (get-context-path "/api/v0/session")
      {:params {:username @(re/subscribe [[:fields :login-page :username]])
      :password @(re/subscribe [[:fields :login-page :password]])}
      :handler #(re/dispatch [[:login :success-handler] %1])
      :error-handler #(re/dispatch [[:error-handler] %1])
      :response-format :json
      :keywords? true})
    db))

(re/reg-event-db
  [:get-rooms]
  (fn [db [_ data]]
    (GET (get-context-path "/api/v0/room")
      {:params data
       :request-format :json
       :handler #(re/dispatch [[:data :rooms] %])
       :error-handler #(re/dispatch [[:error-handler] %1])
       :headers {:authorization (str "Bearer " @(re/subscribe [:session-token]))}
       :response-format :json
       :keywords? true})
    db))

(re/reg-event-db
  [:data :rooms]
  (fn [db [_ value]]
    (assoc-in db [:data :rooms] value)))

(re/reg-event-db
  [:rooms-page :join-room]
  (fn [db [_ data]]
    (POST (get-context-path "/api/v0/participant")
      {:headers         {:authorization (str "Bearer " @(re/subscribe [:session-token]))}
       :params          data
       ;;:handler login-user ;; TODO: set current room as joined
       :error-handler   #(re/dispatch [[:error-handler] %1])
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})})
    db))

;; room create page

(re/reg-event-db
  [:redirect-to-login]
  (fn [db [_ data]]
    (accountant/navigate! "/rooms")
    db))

(re/reg-event-db
  [:create-room]
  (fn [db [_ data]]
    (POST (get-context-path "/api/v0/room")
      {:params {:name @(re/subscribe [[:fields :room-create-page :name]])}
       :headers {:authorization (str "Bearer " @(re/subscribe [:session-token]))}
       :handler #(re/dispatch [[:redirect-to-login] %1])
       :error-handler #(re/dispatch [[:error-handler] %1])})
    db))

(re/reg-event-db
  [:fields :room-create-page :name]
  (fn [db [_ value]]
    (assoc-in db [:fields :room-create-page :name] value)))

;; room page

(re/reg-event-db
  [:data :room :participants]
  (fn [db [_ value]]
    (assoc-in db [:data :room :participants] value)))

(re/reg-event-db
  [:get-participants]
  (fn [db [_ data]]
    (GET (get-context-path "/api/v0/participant")
      {:params data
       :request-format :json
       :handler #(re/dispatch [[:data :room :participants] %])
       :error-handler #(re/dispatch [[:error-handler] %1])
       :headers {:authorization (str "Bearer " @(re/subscribe [:session-token]))}
       :response-format :json
       :keywords? true})
    db))

(re/reg-event-db
  [:data :room :sender]
  (fn [db [_ [value]]]
    (assoc-in db [:data :room :sender] value)))

(re/reg-event-db
  [:get-participant-sender]
  (fn [db [_ data]]
    (GET (get-context-path "/api/v0/participant")
      {:params data
       :request-format :json
       :handler #(re/dispatch [[:data :room :sender] %])
       :error-handler #(re/dispatch [[:error-handler] %1])
       :headers {:authorization (str "Bearer " @(re/subscribe [:session-token]))}
       :response-format :json
       :keywords? true})
    db))

(re/reg-event-db
  [:navbar-mobile :collapsed]
  (fn [db [_ value]]
    (assoc-in db [:navbar-mobile :collapsed] (not value))))

(re/reg-event-db
  [:chat-input :value]
  (fn [db [_ value]]
    (set-chat-input-value! db value)))

;; chat page

(re/reg-event-db
  [:fields :chat-page :room_id]
  (fn [db [_ value]]
    (assoc-in db [:fields :chat-page :room_id] value)))

(re/reg-event-db
  [:fields :chat-page :sender]
  (fn [db [_ value]]
    (assoc-in db [:fields :chat-page :sender] value)))

(re/reg-event-db
  [:fields :chat-page :recipient]
  (fn [db [_ value]]
    (assoc-in db [:fields :chat-page :recipient] value)))

(re/reg-event-db
  [:fields :chat-page :message]
  (fn [db [_ value]]
    (set-chat-input-value! db value)))

(re/reg-event-db
  [:send-message]
  (fn [db [_ data]]
    (POST (get-context-path "/api/v0/message")
      {:params data
       :headers {:authorization (str "Bearer " @(re/subscribe [:session-token]))}
       :handler #(re/dispatch [[:send-message :success] %1])
       :error-handler #(re/dispatch [[:error-handler] %1])})
    db))

(re/reg-event-db
  [:data :room :messages]
  (fn [db [_ value]]
    (prn value)
    (assoc-in db [:data :room :messages] value)))

(re/reg-event-db
  [:get-room-messages]
  (fn [db [_ data]]
    (GET (get-context-path "/api/v0/message")
      {:params data
       :request-format :json
       :handler #(re/dispatch [[:data :room :messages] %])
       :error-handler #(re/dispatch [[:error-handler] %1])
       :headers {:authorization (str "Bearer " @(re/subscribe [:session-token]))}
       :response-format :json
       :keywords? true})
    db))

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