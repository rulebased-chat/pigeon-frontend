(ns pigeon-frontend.context
  (:require-macros [pigeon-frontend.server :refer [get-api-context-env]]))

(defn get-context-path [path]
  (str (get-api-context-env) path))