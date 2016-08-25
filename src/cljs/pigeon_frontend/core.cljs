(ns pigeon-frontend.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]))

;; App state

(def app (reagent/atom 
  {:user {:username "foobar1"
          :password "bar1"
          :full-name "Mr. Foo Bar"}
   :session {:token nil}}))

;; -------------------------
;; Ajax

(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn login-successful [response]
  (swap! app assoc-in [:session :token] (:token response)))

(defn login-user [response]
  (POST "http://localhost:3000/api/v0/session"
    {:params {:username (get-in @app [:user :username])
              :password (get-in @app [:user :password])}
     :handler login-successful
     :error-handler error-handler 
     :response-format :json 
     :keywords? true}))

(defn register-user [_]
  (let [response (PUT "http://localhost:3000/api/v0/user" 
        {:params {:username (get-in @app [:user :username])
                  :password (get-in @app [:user :password])
                  :full_name (get-in @app [:user :full-name])}
         :handler login-user
         :error-handler error-handler})]))

;; -------------------------
;; Views

(defn home-page []
  [layout/layout
    [:div.jumbotron.jumbotron-fluid 
      [:div.container
        [:div.col-sm-12
        [:h2 "pigeon-frontend"]
        [:div [:a.btn.btn-primary {:href "/register"} "Sign up"]]]]]])

(defn register-page []
  [layout/layout
    [:div.row
      [:div.col-sm-12
        [:h2 "Sign up"]
        [:div
          [:p [:input {:name "username" :placeholder "username" 
                       :value (get-in @app [:user :username]) 
                       :on-change #(swap! app assoc-in [:user :username] (-> % .-target .-value))}]]
          [:p [:input {:name "password" :placeholder "password" :type "password" 
                       :value (get-in @app [:user :password]) 
                       :on-change #(swap! app assoc-in [:user :password] (-> % .-target .-value))}]]
          [:p [:input {:name "full_name" :placeholder "full_name" 
                       :value (get-in @app [:user :full-name]) 
                       :on-change #(swap! app assoc-in [:user :full-name] (-> % .-target .-value))}]]
          [:p [:button.btn.btn-default {:type "submit" :on-click register-user} "Submit"]]]]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/register" []
  (session/put! :current-page #'register-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
