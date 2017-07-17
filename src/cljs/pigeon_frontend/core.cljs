(ns pigeon-frontend.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.views.login-page :refer [login-page]]
              [pigeon-frontend.views.chat-page :refer [chat-page]]
              [pigeon-frontend.views.moderator-page :refer [moderator-page]]
              [pigeon-frontend.view-model :refer [app]]
              [re-frame.core :as re]
              [pigeon-frontend.events]
              [pigeon-frontend.subscriptions]
              [reagent.core :as r]
              [hodgepodge.core :refer [local-storage clear!]]
              [cognitect.transit :as t]
              [pigeon-frontend.context :refer [get-context-path]]))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Websocket

(defn receive-transit-msg!
  [update-fn]
  (fn [msg]
    (update-fn
                      ;; todo: fixme
      (->> msg .-data ;; (t/read json-reader)
        ))))

(defn make-websocket! [url receive-handler]
  (println "attempting to connect websocket")
  (if-let [chan (js/WebSocket. url)]
    (do
      (set! (.-onmessage chan) (receive-transit-msg! receive-handler))
      (println "Websocket connection established with: " url))
    (throw (js/Error. "Websocket connection failed!"))))

;; -------------------------
;; Routes

(secretary/defroute "/login" []
  (session/put! :current-page #(partial #'login-page)))

(secretary/defroute "/sender/:sender/recipient/:recipient" {:as params}
  (session/put! :current-page #(partial chat-page params)))

(secretary/defroute "/moderator" []
  ;; todo: moderator username here
  (session/put! :current-page #(partial moderator-page {:sender (get-in local-storage [:session :username])})))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))

(defn initialize-app! [session]
  ;;(re/dispatch-sync [:initialize])
  ;;(re/dispatch-sync [:login session])
  ;; todo: address
  (make-websocket! "ws://localhost:3000/api/v0/ws" #(println %1))
  (init!))