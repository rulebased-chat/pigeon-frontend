(ns pigeon-frontend.views.room-create-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.view-model :refer [app]]
              [pigeon-frontend.context :refer [get-context-path]]
              [pigeon-frontend.views.login-page :refer [login-page]]
              [re-frame.core :as re]))

(defn create-room [event]
  (.preventDefault event)
  (re/dispatch [[:create-room]]))

(defn room-create-page []
  [layout/layout "New room"
                 "A room consists of many people and groups"
    [:div.row
      [:div.col-sm-12
        [:form {:on-submit create-room}
          [:p [:input {:name "name"
                       :placeholder "name"
                       :value @(re/subscribe [[:fields :room-create-page :name]])
                       :on-change #(re/dispatch [[:fields :room-create-page :name] (-> % .-target .-value)])}]]
          [:p [:button.btn.btn-default {:type "submit"} "Submit"]]]]]])