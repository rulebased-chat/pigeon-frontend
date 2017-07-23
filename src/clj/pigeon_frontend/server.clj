(ns pigeon-frontend.server
  (:require [pigeon-frontend.handler :refer [app]]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :as environ])
  (:gen-class))

(defmacro get-api-context-env [] (get environ/env :api-context))
(defmacro get-ws-context-env [] (get environ/env :ws-context))

(defn -main [& args]
  (let [port (Integer/parseInt (or (env :port) "3001"))]
    (run-jetty app {:port port :join? false})))