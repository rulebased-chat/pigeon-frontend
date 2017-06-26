(ns pigeon-frontend.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.views.login-page :refer [login-page]]
              [pigeon-frontend.views.chat-page :refer [chat-page]]
              [pigeon-frontend.view-model :refer [app]]
              [re-frame.core :as re]
              [pigeon-frontend.events]
              [pigeon-frontend.subscriptions]
              [reagent.core :as r]
              [tuck.debug :as debug]
              [tuck.core :as tuck]))

(defn current-page [e! value]
  [:div [((session/get :current-page) e! value)]])

;; -------------------------
;; Routes

(secretary/defroute "/login" []
  (session/put! :current-page #(partial #'login-page)))

(secretary/defroute "/sender/:sender/recipient/:recipient" {:as params}
  (session/put! :current-page #(partial chat-page params)))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [tuck/tuck app current-page] (.getElementById js/document "app")))

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
  (init!))