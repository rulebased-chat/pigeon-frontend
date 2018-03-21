(ns pigeon-frontend.websocket
  (:require-macros
    [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [ajax.core :refer [GET POST PUT DELETE]]
            [taoensso.sente  :as sente :refer (cb-success?)]
            [pigeon-frontend.context :refer [get-context-path
                                             get-ws-context-path]]
            [reagent.session :as session]
            [pigeon-frontend.components :refer [navbar-mobile
                                                navbar
                                                header-height
                                                chat-input
                                                users-to-new-messages
                                                error-container]]
            [hodgepodge.core :refer [local-storage]]))

(defmulti chsk-routes (fn [{:as ev-msg :keys [event]}]
                        (let [[id [broadcast-event]] event]
                          broadcast-event)))

(defmethod chsk-routes :default
  [{:as ev-msg :keys [event]}]
  (println "Unhandled event")
  (cljs.pprint/pprint event))

(defmethod chsk-routes :pigeon/reload-turns
  [{:as ev-msg :keys [?data]}]
  ((session/get :get-turns-fn)))

(defmethod chsk-routes :pigeon/reload-messages
  [{:as ev-msg :keys [?data]}]
  ((session/get :get-messages-fn)))

(defmethod chsk-routes :pigeon/message-received
  [{:as ev-msg :keys [?data]}]
  (let [[_ username] ?data]
    (swap! users-to-new-messages update
      username
      (fn [x]
        (if (nil? x) 1 (inc x))))))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/api/v0/chsk"
        {:type :auto ; e/o #{:auto :ajax :ws}
         :host (clojure.string/replace (get-context-path "") #"http(s)?://" "")
         :params {:username       (get-in local-storage [:session :username])
                  :authorization  (get-in local-storage [:session :token])}})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom

  (sente/start-client-chsk-router! ch-chsk chsk-routes))

