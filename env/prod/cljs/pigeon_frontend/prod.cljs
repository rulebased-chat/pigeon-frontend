(ns pigeon-frontend.prod
  (:require [pigeon-frontend.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
