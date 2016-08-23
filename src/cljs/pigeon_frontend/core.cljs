(ns pigeon-frontend.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]))

;; -------------------------
;; Ajax

(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

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
        [:form {:method "POST" :action "/register"}
          [:p [:input {:name "username" :placeholder "username"}]]
          [:p [:input {:name "password" :placeholder "password" :type "password"}]]
          [:p [:input {:name "full_name" :placeholder "full_name"}]]
          [:p [:input {:type "submit"}]]]]]])

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
