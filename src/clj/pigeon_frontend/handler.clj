(ns pigeon-frontend.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [pigeon-frontend.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
    [:div.loader-container
     [:img {:src "/images/logo.svg"
            :height "38px"
            :width  "38px"}]
     [:p "Loading"]]])

(defn head []
  [:head
   ;; favicons
   [:link {:rel  "apple-touch-icon" :sizes "180x180" :href "/apple-touch-icon.png"}]
   [:link {:rel  "icon" :type "image/png" :sizes "32x32" :href "/favicon-32x32.png"}]
   [:link {:rel  "icon" :type "image/png" :sizes "16x16" :href "/favicon-16x16.png"}]
   [:link {:rel  "manifest" :href "/manifest.json"}]
   [:link {:rel  "mask-icon" :href "/safari-pinned-tab.svg" :color "#5bbad5"}]
   [:title (get env :title)]
   [:meta {:name "theme-color" :content "#ffffff"}]
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))


(defroutes routes
  (GET "/" []      (loading-page))
  (GET "/login" [] (loading-page))
  (GET "/sender/:sender/recipient/:recipient" [_] (loading-page))
  (GET "/moderator" [] (loading-page))

  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
