(ns pigeon-frontend.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.views.register-page :refer [register-page]]
              [pigeon-frontend.views.login-page :refer [login-page]]
              [pigeon-frontend.views.home-page :refer [home-page]]
              [pigeon-frontend.views.rooms-page :refer [rooms-page]]
              [pigeon-frontend.views.room-create-page :refer [room-create-page]]
              [pigeon-frontend.views.room-page :refer [room-page]]
              [pigeon-frontend.views.chat-page :refer [chat-page]]
              [pigeon-frontend.view-model :refer [app]]
              [re-frame.core :as re]
              [pigeon-frontend.events]
              [pigeon-frontend.subscriptions]))

(defn current-page []
  (if (session/get :query-parameters)
    [:div [((session/get :current-page) (session/get :query-parameters))]]
    [:div [(session/get :current-page)]]))

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/login" []
  (session/put! :current-page #'login-page))

(secretary/defroute "/register" []
  (session/put! :current-page #'register-page))

(secretary/defroute "/rooms" []
  (session/put! :current-page #'rooms-page))

(secretary/defroute "/room" []
  (session/put! :current-page #'room-create-page))

(secretary/defroute "/room/:id" {:as params}
  (session/put! :current-page #(partial room-page params)))

(secretary/defroute "/room/:id/user/:user" {:as params}
  (session/put! :current-page #(partial chat-page params)))

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
  (re/dispatch-sync [:initialize])
  (re/dispatch-sync [:login session])
  (init!))