(ns pigeon-frontend.views.room-page
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
            [hodgepodge.core :refer [local-storage clear!]]
            [re-frame.core :as re]
            [pigeon-frontend.components :refer [navbar-mobile
                                                navbar
                                                header-height]]))

(defn room-page [params]
  (let [id (:id params)
        username @(re/subscribe [:session-username])
        room-base-url (str "/room/" id)]
    (prn id username)
    (re/dispatch [[:get-participants] {:room_id id}])
    (re/dispatch [[:get-participant-sender] {:room_id id
                                             :username username}])
    (fn []
      [layout/chat-layout
       [:div.row.h-100
        [navbar-mobile room-base-url
                       @(re/subscribe [[:data :room :sender :id]])
                       @(re/subscribe [[:data :room :participants]])]
        [navbar room-base-url
                (re/subscribe [[:data :room :sender :id]])
                @(re/subscribe [[:data :room :participants]])]
        [:div.col-sm-8.col-md-10.p-0
         [:div.col.col-md-12.p-0 {:style {:overflow "auto"
                                          :height (str "calc(100vh - " header-height " - 5em - " (str @(re/subscribe [[:chat-input :rows]]) "px") ")")}}
          [:div#messages.p-1]]]]])))