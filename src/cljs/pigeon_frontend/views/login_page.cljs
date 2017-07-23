(ns pigeon-frontend.views.login-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.context :refer [get-context-path
                                               get-ws-context-path]]
              [pigeon-frontend.components :refer [users-to-new-messages]]
              [pigeon-frontend.view-model :refer [ws-channel errors]]
              [hodgepodge.core :refer [local-storage clear!]]
              [re-frame.core :as re]
              [cognitect.transit :as transit]))

(def app (reagent/atom {:username ""
                        :password ""}))

;; -------------------------
;; Websocket

(defn receive-transit-msg!
  [update-fn]
  (fn [message]
    (let [reader (transit/reader :json)]
      (update-fn
        (->> message .-data (transit/read reader))))))

(defn make-websocket! [url receive-handler]
  (println "Attempting to connect websocket")
  (try (if-let [chan (js/WebSocket. url)]
         (do
           (reset! ws-channel chan)
           (set! (.-onmessage chan) (receive-transit-msg! receive-handler))
           (println "Websocket connection established with: " url))
         (throw (js/Error. "Websocket connection failed!")))
       (catch js/Error e
         (swap! errors conj {:status 0,
                             :status-text "Websocket failed.",
                             :failure :failed}))))

(defn make-websocket-with-defaults [username]
  (make-websocket! (get-ws-context-path (str "/api/v0/ws/" username))
    (fn [val]
      (if (coll? val)
        (let [[dispatch-key & args] val]
          ;; todo using defmulti & defmethod
          (cond
            (= dispatch-key :reload-turns)      ((session/get :get-turns-fn))
            (= dispatch-key :reload-messages)   ((session/get :get-messages-fn))
            (= dispatch-key :message-received)  (let [[username & _] args]
                                                  (swap! users-to-new-messages update
                                                    username
                                                    (fn [x]
                                                      (if (nil? x) 1 (inc x)))))
            :else val))
        val))))

;; fns & login-page

(defn login-successful [response]
  (assoc! local-storage :session (:session response))
  (make-websocket-with-defaults (get-in local-storage [:session :username]))
  (swap! app assoc :username "")
  (swap! app assoc :password "")
  ;; todo proper address
  (accountant/navigate! "/"))

(defn login-user [response]
  (.preventDefault response)
  (POST (get-context-path "/api/v0/session")
    {:params {:username (get-in @app [:username])
              :password (get-in @app [:password])}
     :handler #(login-successful %1)
     :error-handler #(error-handler %1)
     :response-format :json
     :keywords? true}))

(defn login-page []
  [layout/layout "Log in"
                 "Enter your username and password to sign in"
    [:div.row
      [:div.col-sm-12
        [:form {:method "POST"}
          [:p [:input {:name "username"
                       :placeholder "username"
                       :value (get-in @app [:username])
                       :on-change #(swap! app assoc :username (-> % .-target .-value))}]]
          [:p [:input {:name "password"
                       :placeholder "password" :type "password"
                       :value (get-in @app [:password])
                       :on-change #(swap! app assoc :password (-> % .-target .-value))}]]
          [:p [:button.btn.btn-default {:on-click login-user} "Submit"]]]]]])