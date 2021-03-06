(ns pigeon-frontend.core
    (:require-macros
      [cljs.core.async.macros :as asyncm :refer (go go-loop)])
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.views.login-page :refer [login-page]]
              [pigeon-frontend.views.chat-page :refer [chat-page] :as chat-page]
              [pigeon-frontend.views.front-page :refer [front-page] :as front-page]
              [pigeon-frontend.views.moderator-page :refer [moderator-page] :as moderator-page]
              [pigeon-frontend.view-model :refer [app ws-channel navbar-collapsed? errors]]
              [re-frame.core :as re]
              [pigeon-frontend.events]
              [pigeon-frontend.subscriptions]
              [reagent.core :as r]
              [hodgepodge.core :refer [local-storage clear!]]
              [pigeon-frontend.context :refer [get-context-path
                                               get-ws-context-path]]
              [pigeon-frontend.components :refer [users-to-new-messages]]
              [cljs.core.async :as async :refer (<! >! put! chan)]
              [taoensso.sente  :as sente :refer (cb-success?)]
              [pigeon-frontend.websocket :as websocket]))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
;; Todo: session variables as private atoms

(secretary/defroute "/" []
  (reset! navbar-collapsed? true) ;; todo: probably could hook to some onload listener to reduce duplication...
  (if-let [username (get-in local-storage [:session :username])]
    (do (session/put! :get-turns-fn (partial chat-page/get-turns front-page/app))
        (session/put! :current-page (partial front-page {:username username})))
    (do (session/put! :current-page (fn [_] [:div "Redirecting..."]))
        (accountant/navigate! "/login"))))

(secretary/defroute "/login" []
  (reset! navbar-collapsed? true)
  (session/put! :current-page #(partial #'login-page)))

(secretary/defroute "/sender/:sender/recipient/:recipient" {:as params}
  (reset! navbar-collapsed? true)
  (swap! chat-page/app assoc :messages nil)
  (swap! users-to-new-messages dissoc (:recipient params))
  (session/put! :get-turns-fn    (partial chat-page/get-turns chat-page/app))
  (session/put! :get-messages-fn (partial chat-page/get-messages params))
  (session/put! :current-page    (partial chat-page params)))

(secretary/defroute "/moderator" []
  (reset! navbar-collapsed? true)
  (swap! moderator-page/app assoc :messages nil)
  (session/put! :get-turns-fn moderator-page/get-turns)
  (session/put! :get-messages-fn moderator-page/get-messages)
  (session/put! :current-page (partial moderator-page
                                       {:sender (get-in local-storage [:session :username])})))

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
  (when-let [username (get-in local-storage [:session :username])]
    (websocket/start-chsk!))
  (init!))