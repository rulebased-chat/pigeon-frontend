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

(defn room-page []
  [layout/chat-layout
     [:div.row.h-100
      [:div.col-sm-4.col-md-2.p-0.h-100
       [:div.navbar.navbar-default.p-0.bg-faded.h-100 {:style {:border-radius 0 :border-right "1px solid #d9d9d9" :overflow "auto"}}
        [:ul.list-group
         [:a.list-group-item.text-justify.bg-faded {:href "/room/1/user/foo"
                                                    :style {:border 0 :border-radius 0}}
            "One" [:span.tag.tag-pill.tag-primary.ml-1 {:style {:float "right"}} 1]]
         [:a.list-group-item.text-justify.bg-faded {:href "/room/1/user/bar"
                                                    :style {:border 0 :border-radius 0}}
            "Two" [:span.tag.tag-pill.tag-primary.ml-1 {:style {:float "right"}} 2]]
         [:a.list-group-item.text-justify.bg-faded {:href "/room/1/user/bar"
                                                    :style {:border 0 :border-radius 0}}
            "Three"]]]]
      [:div.col-sm-8.col-md-10.p-0
        [:div.col.col-md-12.p-1 {:style {:height "calc(90vh - 55px)" :overflow "auto"}}
         [:div.col.col-md-6.p-0
          [:p
           [:p.mb-0 (take 5 (repeat "Hello world! "))]
           [:small [:strong "olmorauno"]
            [:span.text-muted.ml-1 "29.01.2017 14:37"]]]]
         [:div.col.col-md-6.p-0.offset-md-6
          [:p
           [:p.mb-0 (take 50 (repeat "ACK "))]
           [:small [:strong "ilmoraunio"]
                   [:span.text-muted.ml-1 "29.01.2017 14:41"]]]]]
        [:div.col.col-md-12.p-0 {:style {:height "calc(10vh - 55px)" :position "relative"}}
         [:textarea.w-100.p-1 {:type "text" :style {:height "calc(10vh - 3px)" :border "none" :resize "none"} :placeholder "Write a message"}]]]]])