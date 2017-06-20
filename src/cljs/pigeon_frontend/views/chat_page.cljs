(ns pigeon-frontend.views.chat-page
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
                                                header-height
                                                chat-input]]))

(defn send-message [event]
  (.preventDefault event)
  (re/dispatch [[:send-message] {:room_id @(re/subscribe [[:fields :chat-page :room_id]])
                                 :sender @(re/subscribe [[:fields :chat-page :sender]])
                                 :recipient @(re/subscribe [[:fields :chat-page :recipient]])
                                 :message @(re/subscribe [[:fields :chat-page :message]])}]))

(defn chat-page [params]
  (let [sender-id (:sender params)
        recipient-id (:recipient params)
        ]
    ;; todo: (re/dispatch [[:fields :chat-page :room_id] (:id params)])
    ;; todo: (re/dispatch [[:fields :chat-page :sender] sender-id])
    ;; todo: (re/dispatch [[:fields :chat-page :recipient] recipient-id])
    ;; todo: (re/dispatch [[:get-participants] {:room_id id}])
    ;; todo: (re/dispatch [[:get-room-messages] {:room_id   id
    ;; todo:                                     :sender    sender-id
    ;; todo:                                     :recipient recipient-id}])
    (fn []
      [layout/chat-layout
       [:div.row.h-100
        [navbar-mobile "foo" '()]
        [navbar "foo" '()]
        [:div.col-sm-8.col-md-10.p-0
         (comment [:div.col.col-md-12.p-0 {:style {:overflow "auto"
                                                   :height (str "calc(100vh - " header-height " - 5em - " (str @(re/subscribe [[:chat-input :rows]]) "px") ")")}}
                   [:div#messages.p-1
                    (for [message '() ;; todo: @(re/subscribe [[:data :room :messages]])
                          ]
                      ^{:key message}
                      (if (:is_from_sender message)
                        ;; todo: probably better to componentize these two
                        [:div.col.col-md-6.p-0.offset-md-6
                         [:p
                          [:p.mb-0 (:message message)]
                          [:small [:strong (:sender_name message)]
                           [:span.text-muted.ml-1 (:updated message)]]]]
                        [:div.col.col-md-6.p-0
                         [:p
                          [:p.mb-0 (:message message)]
                          [:small [:strong (:sender_name message)]
                           [:span.text-muted.ml-1 (:updated message)]]]]))
                    (for [error '() ;; todo: @(re/subscribe [:errors])
                          ]
                      ^{:key error}
                      [:div.alert.alert-danger.alert-dismissible.fade.in {:role "alert"}
                       [:strong (:status-text error)] (str " " (get-in error [:response :title]))
                       [:button.close {:type "button"
                                       :data-dismiss "alert"
                                       :aria-label "Close"
                                       ;; todo: :on-click #(re/dispatch [:remove-error error])
                                       } "x"]])]])
         [chat-input @(re/subscribe [[:chat-input :value]])
          {} ;; todo: {:on-submit send-message}
          {:height (str "calc(5em + " @(re/subscribe [[:chat-input :rows]]) "px)" )}]]]])))