(ns pigeon-frontend.views.moderator-page
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [pigeon-frontend.views.layout :as layout]
            [ajax.core :refer [GET POST PUT DELETE PATCH json-request-format json-response-format]]
            [pigeon-frontend.ajax :refer [error-handler]]
            [pigeon-frontend.context :refer [get-context-path]]
            [dommy.core :refer-macros [sel sel1]]
            [hodgepodge.core :refer [local-storage clear!]]
            [re-frame.core :as re]
            [pigeon-frontend.components :refer [navbar-mobile
                                                navbar
                                                header-height
                                                chat-input
                                                users-to-new-messages]]
            [pigeon-frontend.ajax :refer [error-handler]]))

(def app (reagent/atom {:messages nil
                        :users nil
                        :turns nil
                        :selected-turn nil
                        :users-to-new-messages users-to-new-messages}))

(defn get-turns []
  (GET (get-context-path "/api/v0/turn")
    {:request-format :json
     :handler (fn [vals] (let [turns vals
                               active-turn-id (->> turns
                                                (filter #(:active %))
                                                first
                                                :id)]
                           (swap! app assoc :turns turns :selected-turn active-turn-id)))
     :error-handler #(error-handler %1)
     :response-format :json
     :keywords? true}))

(defn get-messages []
  (GET (get-context-path "/api/v0/message")
    {:request-format :json
     :handler #(swap! app assoc :messages %)
     :error-handler #(error-handler %1)
     :response-format :json
     :keywords? true}))

(defn delete-message [id response]
  (.preventDefault response)
  (DELETE (get-context-path (str "/api/v0/message/" id))
    {:handler get-messages
     :error-handler #(error-handler %1)
     :response-format :json
     :keywords? true}))

(defn undo-delete-message [id response]
  (.preventDefault response)
  (PATCH (get-context-path (str "/api/v0/message/" id))
    {:handler get-messages
     :error-handler #(error-handler %1)
     :response-format :json
     :keywords? true}))

(defn delete-message-attempt [id response]
  (.preventDefault response)
  (DELETE (get-context-path (str "/api/v0/message_attempt/" id))
    {:handler get-messages
     :error-handler #(error-handler %1)
     :response-format :json
     :keywords? true}))

(defn undo-delete-message-attempt [id response]
  (.preventDefault response)
  (PATCH (get-context-path (str "/api/v0/message_attempt/" id))
    {:handler get-messages
     :error-handler #(error-handler %1)
     :response-format :json
     :keywords? true}))

(defn change-turn [id response]
  (.preventDefault response)
  (POST (get-context-path (str "/api/v0/turn/" id))
    {:error-handler #(error-handler %1)
     :response-format :json
     :keywords? true}))

(defn moderator-page [{:keys [sender]}]
  (let [_ (get-messages)
        _ (GET (get-context-path (str "/api/v0/users/" sender))
            {:request-format :json
             :handler #(swap! app assoc :users %)
             :error-handler #(error-handler %1)
             :response-format :json
             :keywords? true})
        _ (get-turns)]
    (fn []
      (let [turn-name (->> (get-in @app  [:turns])
                        (filter #(:active %))
                        first
                        :name)]
        [layout/chat-layout turn-name
         [:div.row.h-100
          [navbar-mobile turn-name sender (get-in @app [:users]) @(get-in @app [:users-to-new-messages])]
          [navbar sender (get-in @app [:users]) @(get-in @app [:users-to-new-messages])]
          [:div.col-sm-8.col-md-10.p-0
           [:form {:on-submit (partial change-turn (get-in @app [:selected-turn]))}
            [:div#change-turn.col.col-md-12.bg-faded.p-1.input-group {:style {:position "absolute"
                                                                              :bottom "0px"
                                                                              :border-top "1px solid #d9d9d9"
                                                                              :height "calc(5em)"}}
             [:select.w-100.custom-select {:style {:height "3em"}
                                           :value (get-in @app [:selected-turn])
                                           :on-change #(swap! app assoc :selected-turn (.-value (.-target %1)))}
              (for [{:keys [id name] :as turn} (get-in @app [:turns])]
                ^{:key turn}
                [:option {:value id} name])]
             [:button.input-group-addon.btn.btn-primary {:type "submit"} "Change turn"]]]
           [:div.col.col-md-12.p-0 {:style {:overflow "auto"
                                            :height (str "calc(100vh - " header-height " - 5em)")}}
            [:div#messages.pt-1.pb-1
             (let [messages-by-message-attempts (group-by :message_attempt (get-in @app [:messages]))]
               (for [[message-attempt-id [{:keys [message_attempt_deleted turn_name]} :as messages] :as entry] messages-by-message-attempts]
                 ^{:key entry}
                 [:div
                  [:div
                   [:hr]
                   [:p.pl-1.pr-1
                    [:span.text-muted.mr-1 (str "Turn " turn_name)]
                    [:span.mr-1 (str "Message attempt #" message-attempt-id)]
                    (if message_attempt_deleted
                      [:a {:href "" :on-click (partial undo-delete-message-attempt message-attempt-id)} "Undo delete"]
                      [:a {:href "" :on-click (partial delete-message-attempt message-attempt-id)} "Delete"])]]
                  (for [message messages]
                    ^{:key message}
                    [:div.col.pl-1.pr-1
                     [:p
                      [:p.mb-0 (:message message)]
                      [:small [:strong (str (:sender_name message)
                                         "→" (:recipient_name message)
                                         "→" (:actual_recipient_name message))]
                       [:span.text-muted.ml-1 (:updated message)]
                       (if (:deleted message)
                         [:a.ml-1 {:href "" :on-click (partial undo-delete-message (:id message))} "Undo delete"]
                         [:a.ml-1 {:href "" :on-click (partial delete-message (:id message))} "Delete"])]]])]))
             (for [error '() ;; todo: @(re/subscribe [:errors])
                   ]
               ^{:key error}
               [:div.alert.alert-danger.alert-dismissible.fade.in {:role "alert"}
                [:strong (:status-text error)] (str " " (get-in error [:response :title]))
                [:button.close {:type "button"
                                :data-dismiss "alert"
                                :aria-label "Close"
                                ;; todo: :on-click #(re/dispatch [:remove-error error])
                                } "x"]])]]]]])
      )))