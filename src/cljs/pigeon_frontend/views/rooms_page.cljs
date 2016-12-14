(ns pigeon-frontend.views.rooms-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE json-request-format json-response-format]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.context :refer [get-context-path]]
              [pigeon-frontend.view-model :refer [app]]
              [dommy.core :refer-macros [sel sel1]]
              [hodgepodge.core :refer [local-storage clear!]]))

(defn set-rooms [response]
  (swap! app assoc-in [:data :rooms] response))

(defn join-room [event data]
  (.preventDefault event)
  (let [response (POST (get-context-path "/api/v0/participant")
                      {:headers {:authorization (str "Bearer " (get-in @app [:session :token]))}
                       :params data
                       ;;:handler login-user ;; TODO: set current room as joined
                       :error-handler error-handler
                       :format (json-request-format)
                       :response-format (json-response-format {:keywords? true})})]))

(defn rooms-page []
  (let [get-rooms (fn [] (GET (get-context-path "/api/v0/room")
                          {:handler set-rooms
                           :error-handler error-handler
                           :headers {:authorization (str "Bearer " (get-in @app [:session :token]))}
                           :response-format :json
                           :keywords? true}))]
    (get-rooms)
    (fn []
      [layout/layout
        "Rooms"
        "Join a room and start a rule-based conversation with your friends or co-workers"
        [:div.container-fluid
          [:p [:a.btn.btn-lg.btn-outline-primary {:href "/room"} "Create a room"]]
          [:div.row
              [:div.col-sm-12
                [:table.table.table-striped
                  [:thead
                    [:tr
                      [:th "Name"]
                      [:th]]]
                  [:tbody
                    (for [room (get-in @app [:data :rooms])]
                      ^{:key room}
                      [:tr [:td (:name room)]
                           [:td [:form {:on-submit #(join-room % {:room_id (:id room)
                                                                  :username (get-in @app [:session :username])
                                                                  :name (get-in @app [:session :username])})}
                                  [:button.btn.btn-outline-success.btn-sm {:type "submit"} "Join room"]]]])]]]]]])))