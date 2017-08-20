(ns pigeon-frontend.views.chat-page
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [pigeon-frontend.views.layout :as layout]
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
            [pigeon-frontend.ajax :refer [error-handler]]
            [cljs.core.async :refer [<! timeout]])
  (:use-macros [cljs.core.async.macros :only [go]]))

(def app (reagent/atom {:sender ""
                        :recipient ""
                        :message ""
                        :messages nil
                        :users nil
                        :message-character-limit nil
                        :users-to-new-messages users-to-new-messages}))

(add-watch app :message-watcher
  (fn [key atom old-state new-state]
    (when (not= (get-in old-state [:messages])
                (get-in new-state [:messages]))
      (if-let [near-enough-bottom? (>= (.-scrollTop (.getElementById js/document "scrollbox"))
                                       (- (.-scrollHeight (.getElementById js/document "messages"))
                                          600))]
        (go (<! (timeout 50))
          (set! (.-scrollTop (.getElementById js/document "scrollbox"))
                (.-scrollHeight (.getElementById js/document "messages"))))))))

(defn chat-input-row-amount [message]
  (let [value message
        rows (count (re-seq #"\n" value))
        rowcap 6
        line-height-in-pixels 18
        rows-or-rowcap (if (< rows rowcap) rows rowcap)]
    (* rows-or-rowcap line-height-in-pixels)))

(defn message-succesful [response]
  (swap! app assoc :message ""))

(defn get-messages [{:keys [sender recipient]}]
  (GET (get-context-path (str "/api/v0/message/sender/" sender "/recipient/" recipient))
    {:request-format :json
     :handler (fn [response] (swap! app assoc :messages response))
     :error-handler #(error-handler %1)
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn get-turns [app]
  (GET (get-context-path "/api/v0/turn")
    {:request-format :json
     :handler #(swap! app assoc :turns %1)
     :error-handler #(error-handler %1)
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn get-message-character-limit [app]
  (GET (get-context-path "/api/v0/message_character_limit")
    {:request-format :json
     :handler (fn [{:keys [message-character-limit]}]
                (swap! app assoc :message-character-limit message-character-limit))
     :error-handler #(error-handler %1)
     :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
     :response-format :json
     :keywords? true}))

(defn send-message [{:keys [sender recipient]} event]
  (.preventDefault event)
  (when (not-empty (:message @app))
    (POST (get-context-path (str "/api/v0/message/sender/" sender "/recipient/" recipient))
      {:params {:message (:message @app)}
       :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
       :handler #(message-succesful %1)
       :error-handler #(error-handler %1)})))

(defn chat-page [{:keys [sender recipient] :as params} users-to-new-messages]
  (let [_ (get-messages params)
        _ (GET (get-context-path (str "/api/v0/users/" sender))
               {:request-format :json
                :handler #(swap! app assoc :users %)
                :error-handler #(error-handler %1)
                :headers {:authorization (str "Bearer " (get-in local-storage [:session :token]))}
                :response-format :json
                :keywords? true})
        _ (get-turns app)
        _ (get-message-character-limit app)]
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
           [:div#scrollbox.col.col-md-12.p-0 {:style {:overflow "auto"
                                            :height (str "calc(100vh - " header-height " - 5em - " (str @(re/subscribe [[:chat-input :rows]]) "px") ")")}}
            [:div#messages.p-1
             (let [messages-by-turns (group-by :turn (get-in @app [:messages]))]
               (for [[_ [{:keys [turn_name]} :as messages] :as entry] messages-by-turns]
                 ^{:key entry}
                 [:div
                  [:div.mt-2.mb-1.text-muted {:style {:border-bottom "1px solid #efefef"
                                                      :line-height "0.1em"
                                                      :text-align "center"}}
                   [:small.px-1 {:style {:background-color "#fff"
                                         :text-transform "uppercase"}} (str "Turn " turn_name)]]
                  (for [message messages]
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
                         [:span.text-muted.ml-1 (:updated message)]]]]))]))
             (for [error @errors]
               ^{:key error}
               [error-container error])]]
           [chat-input app ;;@(re/subscribe [[:chat-input :value]])
            {:on-click (partial send-message {:sender sender
                                              :recipient recipient})}
            {:height (str "calc(5em + " (chat-input-row-amount (:message @app)) "px)" )}]]]]))))