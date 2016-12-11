(ns pigeon-frontend.prod
  (:require [pigeon-frontend.core :as core]
            [hodgepodge.core :refer [local-storage clear!]]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/initialize-app! (:session local-storage))
