(ns pigeon-frontend.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.webjars :refer [wrap-webjars]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-defaults (assoc-in site-defaults [:security :hsts] true))
      wrap-exceptions
      wrap-reload
      wrap-webjars))
