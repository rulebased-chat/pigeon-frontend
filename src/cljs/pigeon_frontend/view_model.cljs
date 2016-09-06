(ns pigeon-frontend.view-model
    (:require [reagent.core :as reagent :refer [atom]]))

(def app
  (reagent/atom
    {:session {:token nil}
     :fields {:register-page {:username ""
                              :password ""
                              :full-name ""}
              :login-page {:username ""
                           :password ""}
              :rooms-page {}}
     :data {:rooms [{:name "foo"} {:name "bar"}]}}))