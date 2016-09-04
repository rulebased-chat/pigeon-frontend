(ns pigeon-frontend.view-model
    (:require [reagent.core :as reagent :refer [atom]]))

;; App state

(def app (reagent/atom 
  {:user {:username "foobar1"
          :password "bar1"
          :full-name "Mr. Foo Bar"}
   :session {:token nil}
   :fields {:user {:username "foobar2"
                   :password "bar2"
                   :full-name "Mr. Fubar"}}}))