(ns pigeon-frontend.views.login-page
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.ajax :refer [error-handler]]
              [pigeon-frontend.view-model :refer [app]]
              [pigeon-frontend.context :refer [get-context-path]]
              [hodgepodge.core :refer [local-storage clear!]]
              [re-frame.core :as re]
              [tuck.core :refer [tuck wrap wrap-path send-value! Event process-event
                                 action!
                                 send-async!]]
              [tuck.debug :as debug]))

(defrecord LoginSuccesfully [username password])
(defrecord LoginSuccesfullyResult [result])

(comment (extend-protocol Event
           LoginSuccesfully
           (process-event [{username :username
                            password :password} app]
             (action! (fn [e!]
                        (POST "http://localhost:3000/api/v0/session"
                          {:response-format :json
                           :handler #(e! (->LoginSuccesfullyResult %))}))))

           LoginSuccesfullyResult
           (process-event [result app]
             (.log js/console "GOT RESULT: " (pr-str result))
             (.log js/console "app: " (pr-str app))
             ;;(accountant/navigate! "/sender/foo/recipient/bar")
             )))

(defn login-successful [response]
  ;; todo: would probably be better if stored in a browser cookie with HttpOnly enabled
  (re/dispatch [:login (:session response)])
  (assoc! local-storage :session (:session response))
  (accountant/navigate! "/rooms"))

(defn login-user [response]
  (.preventDefault response)
  (re/dispatch [[:attempt-login]]))

(defn login-page [e! value]
  [layout/layout "Log in"
                 "Enter your username and password to sign in"
    [:div.row
      [:div.col-sm-12
        [:form ;; todo: {:on-submit login-user}
          [:p [:input {:name "username"
                       :placeholder "username"
                       ;; todo: :value @(re/subscribe [[:fields :login-page :username]])
                       ;; todo: :on-change #(re/dispatch [[:fields :login-page :username] (-> % .-target .-value)])
                       }]]
          [:p [:input {:name "password"
                       :placeholder "password" :type "password"
                       ;; todo: :value @(re/subscribe [[:fields :login-page :password]])
                       ;; todo: :on-change #(re/dispatch [[:fields :login-page :password] (-> % .-target .-value)])
                       }]]
          [:p [:button.btn.btn-default {:type "submit"} "Submit"]]]]]])