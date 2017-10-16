(ns pigeon-frontend.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.middleware.ssl :refer [wrap-forwarded-scheme]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      wrap-exceptions
      wrap-reload
      wrap-webjars
      wrap-forwarded-scheme))
