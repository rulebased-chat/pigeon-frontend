(ns pigeon-frontend.ajax
  (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [pigeon-frontend.views.layout :as layout]
              [ajax.core :refer [GET POST PUT DELETE]]
              [pigeon-frontend.view-model :refer [app]]))

(defn handler [response]
  (.log js/console (str response)))

;; TODO: handle errors with defmulti based on http statuses
(defn error-handler [{:keys [status status-text] :as response}]
  (swap! app update-in [:errors] conj response)
  (.log js/console "Bad response" (str response)))