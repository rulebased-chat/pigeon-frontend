(ns pigeon-frontend.view-model
    (:require [reagent.core :as reagent :refer [atom]]))

(def app
  (reagent/atom
    {:session {:token nil :username nil}
     :fields {:register-page {:username ""
                              :password ""
                              :full-name ""}
              :login-page {:username ""
                           :password ""}
              :rooms-page {}
              :room-create-page {:name ""}}
     :data {:rooms [{:name "placeholder room"}]
            :room {:participants []}}
     :navbar-mobile {:collapsed true}
     :chat-input {:value ""}
     :errors #{}}))