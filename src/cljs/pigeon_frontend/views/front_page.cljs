(ns pigeon-frontend.views.front-page
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [pigeon-frontend.views.layout :as layout]
            [pigeon-frontend.views.chat-page :refer [get-turns]]
            [ajax.core :refer [GET POST PUT DELETE json-request-format json-response-format]]
            [pigeon-frontend.ajax :refer [error-handler]]
            [pigeon-frontend.view-model :refer [errors]]
            [pigeon-frontend.context :refer [get-context-path]]
            [dommy.core :refer-macros [sel sel1]]
            [hodgepodge.core :refer [local-storage clear!]]
            [re-frame.core :as re]
            [pigeon-frontend.components :refer [navbar-mobile
                                                navbar
                                                header-height
                                                chat-input
                                                users-to-new-messages
                                                error-container]]
            [pigeon-frontend.ajax :refer [error-handler]]))

(def app (reagent/atom {:sender ""
                        :recipient ""
                        :message ""
                        :messages nil
                        :users nil
                        :users-to-new-messages users-to-new-messages}))

(defn front-page [{:keys [username]}]
  ;; todo: (re/dispatch [[:fields :chat-page :room_id] (:id params)])
  ;; todo: (re/dispatch [[:fields :chat-page :sender] sender-id])
  ;; todo: (re/dispatch [[:fields :chat-page :recipient] recipient-id])
  ;; todo: (re/dispatch [[:get-participants] {:room_id id}])
  ;; todo: (re/dispatch [[:get-room-messages] {:room_id   id
  ;; todo:                                     :sender    sender-id
  ;; todo:                                     :recipient recipient-id}])
  (let [_ (GET (get-context-path (str "/api/v0/users/" username))
               {:request-format :json
                :handler #(swap! app assoc :users %)
                :error-handler #(error-handler %1)
                :response-format :json
                :keywords? true})
        _ (get-turns app)]
    (fn []
      (let [turn-name (->> (get-in @app  [:turns])
                           (filter #(:active %))
                           first
                           :name)]
        [layout/chat-layout turn-name
         [:div.row.h-100
          [navbar-mobile
            turn-name
            username
            (get-in @app [:users])
            @(get-in @app [:users-to-new-messages])]
          [navbar
            username
            (get-in @app [:users])
            @(get-in @app [:users-to-new-messages])]
          [:div.col-sm-8.col-md-10.p-0
           [:div.col.col-md-12.p-0 {:style {:overflow "auto"
                                            :height (str "calc(100vh - " header-height " - 5em - " (str @(re/subscribe [[:chat-input :rows]]) "px") ")")}}
            [:div#messages.p-1
             (for [error @errors]
               ^{:key error}
               [error-container error])]]]]]))))