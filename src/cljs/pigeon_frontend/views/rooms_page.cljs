(ns pigeon-frontend.views.rooms-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.context :refer [get-context-path]]
              [pigeon-frontend.view-model :refer [app]]))

(defn set-rooms [response]
  (.log js/console response)
  (swap! app assoc-in [:data :rooms] response))

(GET (get-context-path "/api/v0/room")
  {:handler set-rooms
   :error-handler error-handler
   :response-format :json
   :keywords? true})

(defn rooms-page []
  [layout/layout "Rooms"
                   "Join a room and start a rule-based conversation with your friends or co-workers"
      [:div.container-fluid
        [:div.row
            [:div.col-sm-12
              [:table.table.table-striped
                [:thead
                  [:tr
                    [:th "Name"]]]
                [:tbody
                  (for [room (get-in @app [:data :rooms])]
                    ^{:key (:name room)}
                    [:tr [:td (:name room)]])]]]]]])