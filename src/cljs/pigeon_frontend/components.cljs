(ns pigeon-frontend.components
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [ajax.core :refer [GET POST PUT DELETE json-request-format json-response-format]]
            [pigeon-frontend.ajax :refer [error-handler]]
            [pigeon-frontend.context :refer [get-context-path]]
            [pigeon-frontend.view-model :refer [app navbar-collapsed? errors]]
            [dommy.core :refer-macros [sel sel1]]
            [hodgepodge.core :refer [local-storage clear!]]
            [re-frame.core :as re]))

(def header-height "55px")
(def users-to-new-messages (atom {}))

(defn- pathname []
  (clojure.string/replace (->> js/window .-location .-href)
    (str (->> js/window .-location .-protocol)
      "//"
      (->> js/window .-location .-host))
    ""))

(defn navbar-entries [sender participants users-to-new-messages is-moderator?]
  [:ul.list-group
   [:a.list-group-item.text-justify.bg-faded {:href "/"
                                              :style {:border 0 :border-radius 0}}
    "Go back"]
   (when is-moderator?
     [:a.list-group-item.text-justify.bg-faded {:class (when (= (pathname) "/moderator") "active")
                                                :href "/moderator"
                                                :style {:border 0 :border-radius 0}}
      "Moderator"])
   (for [{:keys [username name] :as participant} participants]
     ^{:key participant}
     (let [href (str "/sender/" sender "/recipient/" username)]
       [:a.list-group-item.text-justify.bg-faded {:class (when (= (pathname) href) "active")
                                                  :href href
                                                  :style {:border 0 :border-radius 0}}
        name
        (when-let [new-message-count (get users-to-new-messages username)]
          [:span.tag.tag-pill.tag-primary.ml-1 {:style {:float "right"}} new-message-count])]))])

(defn navbar [sender participants users-to-new-messages is-moderator?]
  [:div.col-sm-4.col-md-2.p-0.h-100.hidden-xs-down
   [:div.navbar.navbar-default.p-0.bg-faded.h-100 {:style {:border-radius 0 :border-right "1px solid #d9d9d9" :overflow "auto" :z-index 1}}
    [navbar-entries sender participants users-to-new-messages is-moderator?]]])

(defn navbar-mobile [turn-name sender-id participants users-to-new-messages is-moderator?]
  [:div.col-xs-12.p-0.hidden-sm-up {:style {:display (if @navbar-collapsed? "none" "block")
                                            :height (str "calc(100vh - " header-height ")")
                                            :position "absolute"
                                            :z-index "1000"}}
   [:div.navbar.navbar-default.p-0.bg-faded.h-100.bg-faded {:style {:border-radius 0 :border-right "1px solid #d9d9d9" :overflow "auto"}}
    [:ul.list-group
     [:span.list-group-item.text-justify.bg-faded {:style {:border 0 :border-radius 0}} turn-name]]
    [navbar-entries sender-id participants users-to-new-messages is-moderator?]]])

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
    (when-let [message-character-limit (:message-character-limit @app)]
      [:button.input-group-addon {:disabled true}
       (- message-character-limit (count (:message @app)))])
    [:button.input-group-addon.btn.btn-primary (merge {:type "submit"} on-click-action) "Send"]]])

(defn error-container [error]
  [:div.alert.alert-danger.alert-dismissible.fade.in {:role "alert"}
   [:strong (:status-text error)] (str " " (get-in error [:response :title]))
   [:button.close {:type "button"
                   :data-dismiss "alert"
                   :aria-label "Close"
                   :on-click #(swap! errors disj error)} "x"]])

(defn logo []
  [:img {:src "/images/logo.svg"
         :style {:height "38px"}}])

(defn logo-centered []
  [:img {:src "/images/logo.svg"
         :style {:height "38px"
                 :position "absolute"
                 :left "calc(50% - 19px)"}}])