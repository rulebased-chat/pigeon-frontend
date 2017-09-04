(ns pigeon-frontend.context
  (:require-macros [pigeon-frontend.server :as server :refer [get-api-context-env
                                                              get-ws-context-env]]))

(defn get-context-path [path]
  (str (get-api-context-env) path))

(defn get-ws-context-path [path]
  (str (get-ws-context-env) path))

(defn get-title []
  (server/get-title))