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

(defn navbar-entries [sender participants]
  [:ul.list-group
   [:a.list-group-item.text-justify.bg-faded {:href "/login"
                                              :style {:border 0 :border-radius 0}}
    "Go back"]
   (for [{:keys [username name]} participants]
     [:a.list-group-item.text-justify.bg-faded {:href (str "/sender/" sender "/recipient/" username)
                                                :style {:border 0 :border-radius 0}}
      name])
   [:a.list-group-item.text-justify.bg-faded {:href (str "/sender/foo/recipient/bar")
                                              :style {:border 0 :border-radius 0}}
    "test" [:span.tag.tag-pill.tag-primary.ml-1 {:style {:float "right"}} 2]]
   [:a.list-group-item.active.text-justify.bg-faded {:href (str "/sender/foo/recipient/bar")
                                                     :style {:border 0 :border-radius 0}}
    "test (selected)" [:span.tag.tag-pill.tag-primary.ml-1 {:style {:float "right"}} 1]]
   ])

(defn navbar [sender participants]
  [:div.col-sm-4.col-md-2.p-0.h-100.hidden-xs-down
   [:div.navbar.navbar-default.p-0.bg-faded.h-100 {:style {:border-radius 0 :border-right "1px solid #d9d9d9" :overflow "auto" :z-index 1}}
    [navbar-entries sender participants]]])

(defn navbar-mobile [sender-id participants]
  [:div.col-xs-12.p-0.hidden-sm-up {:style {:display @(re/subscribe [[:navbar-mobile :display]])
                                            :height (str "calc(100vh - " header-height ")")
                                            :position "absolute"
                                            :z-index "1000"}}
   [:div.navbar.navbar-default.p-0.bg-faded.h-100.bg-faded {:style {:border-radius 0 :border-right "1px solid #d9d9d9" :overflow "auto"}}
    [navbar-entries sender-id participants]]])

(defn chat-input [app on-click-action style-opts]
  [:form
   [:div#chat-input.col.col-md-12.bg-faded.p-1.input-group {:style (merge {:position "absolute"
                                                                           :bottom "0px"
                                                                           :border-top "1px solid #d9d9d9"
                                                                           :box-shadow "0px 10000px 0px 10000px #f7f7f9"}
                                                                     style-opts)}
    [:textarea.w-100.rounded-left {:type "text"
                                   :style {:border "1px solid #d9d9d9" :resize "none"}
                                   :placeholder "Write a message"
                                   :on-change #(swap! app assoc :message (-> % .-target .-value))
                                   :value (get-in @app [:message])}]
    [:button.input-group-addon.btn.btn-primary (merge {:type "submit"} on-click-action) "Send"]]])
