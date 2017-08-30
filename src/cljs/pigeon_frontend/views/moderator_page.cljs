(ns pigeon-frontend.views.moderator-page
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [pigeon-frontend.views.layout :as layout]
            [ajax.core :refer [GET POST PUT DELETE PATCH json-request-format json-response-format]]
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
            [pigeon-frontend.ajax :refer [error-handler]]
            [cljs.core.async :refer [<! timeout]])
  (:use-macros [cljs.core.async.macros :only [go]]))

(def app (reagent/atom {:messages nil
                        :users nil
                        :turns nil
                        :selected-turn nil
                        :users-to-new-messages users-to-new-messages}))

(add-watch app :message-watcher
  (fn [key atom old-state new-state]
    (when (and (empty? (get-in old-state [:messages]))
               (not-empty (get-in new-state [:messages])))
      (if-let [near-enough-bottom? (>= (.-scrollTop (.getElementById js/document "scrollbox"))
                                       (- (.-scrollHeight (.getElementById js/document "messages"))
                                       600))]
        (go (<! (timeout 50))
          (set! (.-scrollTop (.getElementById js/document "scrollbox"))
            (.-scrollHeight (.getElementById js/document "messages"))))))))

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
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn get-messages []
  (GET (get-context-path "/api/v0/message")
    {:request-format :json
     :handler #(swap! app assoc :messages %)
     :error-handler #(error-handler %1)
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn delete-message [id response]
  (.preventDefault response)
  (DELETE (get-context-path (str "/api/v0/message/" id))
    {:handler get-messages
     :error-handler #(error-handler %1)
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn undo-delete-message [id response]
  (.preventDefault response)
  (PATCH (get-context-path (str "/api/v0/message/" id))
    {:handler get-messages
     :error-handler #(error-handler %1)
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn delete-message-attempt [id response]
  (.preventDefault response)
  (DELETE (get-context-path (str "/api/v0/message_attempt/" id))
    {:handler get-messages
     :error-handler #(error-handler %1)
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn undo-delete-message-attempt [id response]
  (.preventDefault response)
  (PATCH (get-context-path (str "/api/v0/message_attempt/" id))
    {:handler get-messages
     :error-handler #(error-handler %1)
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn change-turn [id response]
  (.preventDefault response)
  (POST (get-context-path (str "/api/v0/turn/" id))
    {:error-handler #(error-handler %1)
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn moderator-page [{:keys [sender]}]
  (let [_ (get-messages)
        _ (GET (get-context-path (str "/api/v0/users/" sender))
            {:request-format :json
             :handler #(swap! app assoc :users %)
             :error-handler #(error-handler %1)
             :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
             :response-format :json
             :keywords? true})
        _ (get-turns)]
    (fn []
      (let [active-turn-name (->> (get-in @app  [:turns])
                                  (filter #(:active %))
                                  first
                                  :name)]
        [layout/chat-layout active-turn-name
         [:div.row.h-100
          [navbar-mobile
            active-turn-name
            sender
            (get-in @app [:users])
            @(get-in @app [:users-to-new-messages])
            (get-in local-storage [:session :is_moderator])]
          [navbar
            sender
            (get-in @app [:users])
            @(get-in @app [:users-to-new-messages])
            (get-in local-storage [:session :is_moderator])]
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
             [:button.input-group-addon.btn.btn-secondary {:type "submit"} "Change turn"]]]
           [:div#scrollbox.col.col-md-12.p-0 {:style {:overflow "auto"
                                            :height (str "calc(100vh - " header-height " - 5em)")}}
            [:div#messages.pt-1.pb-1
             (let [messages-by-message-attempts (sort-by key (group-by :message_attempt (get-in @app [:messages])))]
               (for [[message-attempt-id [{:keys [message_attempt_deleted turn_name]} :as messages] :as entry] messages-by-message-attempts]
                 ^{:key entry}
                 [:div
                  [:div
                   [:hr]
                   [:p.pl-1.pr-1
                    [:span.text-muted.mr-1 (str "Turn " turn_name)]
                    [:span.mr-1 (str "Message attempt #" message-attempt-id)]
                    (if message_attempt_deleted
                      [:button.btn.btn-sm.btn-outline-success {:on-click (partial undo-delete-message-attempt message-attempt-id)} "Undo delete"]
                      [:button.btn.btn-sm.btn-outline-danger {:on-click (partial delete-message-attempt message-attempt-id)} "Delete"])]]
                  (for [message messages]
                    ^{:key message}
                    [:div.col.pl-1.pr-1
                     [:p
                      [:p.mb-0 (:message message)]
                      [:small [:strong (str (:sender_name message)
                                         "→" (:recipient_name message)
                                         "→" (:actual_recipient_name message))]
                       [:span.text-muted.ml-1.mr-1 (:updated message)]
                       (if (:deleted message)
                         [:button.btn.btn-sm.btn-outline-success {:on-click (partial undo-delete-message (:id message))} [:small "Undo delete"]]
                         [:button.btn.btn-sm.btn-outline-danger {:on-click (partial delete-message (:id message))} [:small "Delete"]])]]])]))
             [:div.px-1 (for [error @errors]
                          ^{:key error}
                          [error-container error])]]]]]]))))