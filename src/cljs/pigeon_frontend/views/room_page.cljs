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
            [re-frame.core :as re]))

(def ^:private header-height "55px")

(defn navbar-entries []
  [:ul.list-group
   [:a.list-group-item.text-justify.bg-faded {:href "/rooms"
                                              :style {:border 0 :border-radius 0}}
    "Go back"]
   [:a.list-group-item.active.text-justify.bg-faded {:href "/room/1/user/foo"
                                                     :style {:border 0 :border-radius 0}}
    "One" [:span.tag.tag-pill.tag-primary.ml-1 {:style {:float "right"}} 1]]
   [:a.list-group-item.text-justify.bg-faded {:href "/room/1/user/bar"
                                              :style {:border 0 :border-radius 0}}
    "Two" [:span.tag.tag-pill.tag-primary.ml-1 {:style {:float "right"}} 2]]
   [:a.list-group-item.text-justify.bg-faded {:href "/room/1/user/bar"
                                              :style {:border 0 :border-radius 0}}
    "Three"]])

(defn navbar []
  [:div.col-sm-4.col-md-2.p-0.h-100.hidden-xs-down
   [:div.navbar.navbar-default.p-0.bg-faded.h-100 {:style {:border-radius 0 :border-right "1px solid #d9d9d9" :overflow "auto"}}
    [navbar-entries]]])

(defn navbar-mobile []
  [:div.col-xs-12.p-0.hidden-sm-up {:style {:display @(re/subscribe [[:navbar-mobile :display]])
                                            :height (str "calc(100vh - " header-height ")")
                                            :position "absolute"
                                            :z-index "1000"}}
   [:div.navbar.navbar-default.p-0.bg-faded.h-100.bg-faded {:style {:border-radius 0 :border-right "1px solid #d9d9d9" :overflow "auto"}}
    [navbar-entries]]])

(defn chat-input []
  [:div#chat-input.col.col-md-12.bg-faded.p-1.input-group {:style {:border-top "1px solid #d9d9d9"
                                                                   :box-shadow "0px 10000px 0px 10000px #f7f7f9"}}
   [:textarea.w-100.rounded-left {:type "text"
                                  :rows @(re/subscribe [[:chat-input :rows]])
                                  :style {:border "1px solid #d9d9d9" :resize "none"}
                                  :placeholder "Write a message"
                                  :on-change #(re/dispatch [[:chat-input :value] (->  % .-target .-value)])}
    @(re/subscribe [[:chat-input :value]])]
   [:span.input-group-addon.btn.btn-primary {:style {:min-height "2em"}} "Send"]])

(defn room-page []
  [layout/chat-layout
     [:div.row.h-100
      [navbar-mobile]
      [navbar]
      [:div.col-sm-8.col-md-10.p-0
        [:div.col.col-md-12.p-0 {:style {:overflow "auto"
                                         :height (str "calc(100vh - " header-height ")")}}
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
                           :aria-label "Close"} "x"]]]
         [chat-input]]]]])