(ns pigeon-frontend.views.rooms-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.context :refer [get-context-path]]))

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
                [:tr
                  [:td "Room 1"]]
                [:tr
                  [:td "Room 2"]]]]]]]])