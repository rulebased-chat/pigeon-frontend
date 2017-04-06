(ns pigeon-frontend.components
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

(def header-height "55px")

(defn navbar-entries [room-base-url sender-id participants]
  [:ul.list-group
   [:a.list-group-item.text-justify.bg-faded {:href "/rooms"
                                              :style {:border 0 :border-radius 0}}
    "Go back"]
   (for [participant participants]
     [:a.list-group-item.text-justify.bg-faded {:href (str room-base-url "/sender/" sender-id "/recipient/" (:id participant))
                                                :style {:border 0 :border-radius 0}}
      (:name participant)])
   [:a.list-group-item.active.text-justify.bg-faded {:href (str room-base-url "/sender/foo/recipient/bar")
                                                     :style {:border 0 :border-radius 0}}
    "One" [:span.tag.tag-pill.tag-primary.ml-1 {:style {:float "right"}} 1]]
   [:a.list-group-item.text-justify.bg-faded {:href (str room-base-url "/sender/foo/recipient/bar")
                                              :style {:border 0 :border-radius 0}}
    "Two" [:span.tag.tag-pill.tag-primary.ml-1 {:style {:float "right"}} 2]]
   [:a.list-group-item.text-justify.bg-faded {:href (str room-base-url "/sender/foo/recipient/bar")
                                              :style {:border 0 :border-radius 0}}
    "Three"]])

(defn navbar [room-base-url sender-id participants]
  [:div.col-sm-4.col-md-2.p-0.h-100.hidden-xs-down
   [:div.navbar.navbar-default.p-0.bg-faded.h-100 {:style {:border-radius 0 :border-right "1px solid #d9d9d9" :overflow "auto" :z-index 1}}
    [navbar-entries room-base-url sender-id participants]]])

(defn navbar-mobile [room-base-url sender-id participants]
  [:div.col-xs-12.p-0.hidden-sm-up {:style {:display @(re/subscribe [[:navbar-mobile :display]])
                                            :height (str "calc(100vh - " header-height ")")
                                            :position "absolute"
                                            :z-index "1000"}}
   [:div.navbar.navbar-default.p-0.bg-faded.h-100.bg-faded {:style {:border-radius 0 :border-right "1px solid #d9d9d9" :overflow "auto"}}
    [navbar-entries room-base-url sender-id participants]]])

(defn chat-input [form-map style-opts]
  [:form form-map
   [:div#chat-input.col.col-md-12.bg-faded.p-1.input-group {:style (merge {:position "absolute"
                                                                           :bottom "0px"
                                                                           :border-top "1px solid #d9d9d9"
                                                                           :box-shadow "0px 10000px 0px 10000px #f7f7f9"}
                                                                     style-opts)}
    [:textarea.w-100.rounded-left {:type "text"
                                   :style {:border "1px solid #d9d9d9" :resize "none"}
                                   :placeholder "Write a message"
                                   :on-change #(re/dispatch [[:fields :chat-page :message] (-> % .-target .-value)])}
     @(re/subscribe [[:chat-input :value]])]
    [:button.input-group-addon.btn.btn-primary {:type "submit"} "Send"]]])
