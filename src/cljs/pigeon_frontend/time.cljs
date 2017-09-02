(ns pigeon-frontend.time
  (:require [cljs-time.core :as time-core]
            [cljs-time.format :as time-format]))

(def zulu-formatter (time-format/formatter "yyyy-MM-dd'T'HH:mm:ss'Z'"))
(def local-datetime-formatter (time-format/formatter-local "E do MMM yyyy',' HH:mm"))
(def local-day-of-week-plus-time-formatter (time-format/formatter-local "E HH:mm"))
(def local-time-formatter (time-format/formatter-local "HH:mm"))
(def timezone-difference (* -1 (.getTimezoneOffset (js/Date.))))

(defn parse-zulu [zulu]
  (time-format/parse zulu-formatter zulu))

(defn to-local-time-str [datetime]
  (let [local-datetime (time-core/plus datetime (time-core/minutes timezone-difference))
        today?         (time-core/after? local-datetime (js/Date.))
        within-a-week? (time-core/after? local-datetime (time-core/plus (time-core/today 00 00)
                                                          (time-core/days -6)))]
    (cond
      today?         (time-format/unparse local-time-formatter local-datetime)
      within-a-week? (time-format/unparse local-day-of-week-plus-time-formatter local-datetime)
      :else          (time-format/unparse local-datetime-formatter local-datetime))))
