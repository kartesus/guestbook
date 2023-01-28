(ns guestbook.validation
  (:require [struct.core :as st]))

(defn max-length [len]
  {:message (str "must be at least " len " characters long")
   :validate (fn [msg] (>= (count msg) len))})

(def message-schema
  [[:name st/required st/string]
   [:message st/required st/string (max-length 5)]])

(defn validate-params [params]
  (first (st/validate params message-schema)))
