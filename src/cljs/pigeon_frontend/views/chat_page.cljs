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
  (re/dispatch [[:send-message]]))

(defn chat-page [params]
  (let [id (:id params)
        room-base-url (str "/room/" id)]
    (re/dispatch [[:get-participants] {:room_id id}])
    (fn []
      [layout/chat-layout
       [:div.row.h-100
        [navbar-mobile room-base-url @(re/subscribe [[:data :room :participants]])]
        [navbar room-base-url @(re/subscribe [[:data :room :participants]])]
        [:div.col-sm-8.col-md-10.p-0
         [:div.col.col-md-12.p-0 {:style {:overflow "auto"
                                          :height (str "calc(100vh - " header-height " - 5em - " (str @(re/subscribe [[:chat-input :rows]]) "px") ")")}}
          [:div#messages.p-1
           ;; example
           ;; sent by someone else
           [:div.col.col-md-6.p-0
            [:p
             [:p.mb-0 (take 5 (repeat "Hello world! "))]
             [:small [:strong "olmorauno"]
              [:span.text-muted.ml-1 "29.01.2017 14:37"]]]]
           ;; example
           ;; sent by user
           [:div.col.col-md-6.p-0.offset-md-6
            [:p
             [:p.mb-0 (take 50 (repeat "ACK "))]
             [:small [:strong "ilmoraunio"]
              [:span.text-muted.ml-1 "29.01.2017 14:41"]]]]
           (take 10 (repeat [:div.col.col-md-6.p-0
                             [:p
                              [:p.mb-0 (take 5 (repeat "Hello world! "))]
                              [:small [:strong "olmorauno"]
                               [:span.text-muted.ml-1 "29.01.2017 14:37"]]]]))
           (for [error @(re/subscribe [:errors])]
             ^{:key error}
             [:div.alert.alert-danger.alert-dismissible.fade.in {:role "alert"}
              [:strong (:status-text error)] (str " " (get-in error [:response :title]))
              [:button.close {:type "button"
                              :data-dismiss "alert"
                              :aria-label "Close"
                              :on-click #(re/dispatch [:remove-error error])} "x"]])
           ;; example
           [:div.alert.alert-danger.alert-dismissible.fade.in {:role "alert"}
            [:strong "Something went wrong"] (str " Please try again.")
            [:button.close {:type "button"
                            :data-dismiss "alert"
                            :aria-label "Close"} "x"]]]]
         [chat-input {:on-submit send-message}
                     {:height (str "calc(5em + " @(re/subscribe [[:chat-input :rows]]) "px)" )}]]]])))