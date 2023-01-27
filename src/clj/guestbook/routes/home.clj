(ns guestbook.routes.home
  (:require
   [guestbook.layout :as layout]
   [guestbook.db.core :as db]
   [guestbook.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [struct.core :as st]))

(defn max-length [len]
  {:message (str "must be at least " len " characters long")
   :validate (fn [msg] (>= (count msg) len))})

(def message-schema
  [[:name st/required st/string]
   [:message st/required st/string (max-length 5)]])

(defn validate-params [params]
  (first (st/validate params message-schema)))

(defn home-page [{:keys [flash] :as request}]
  (layout/render request
                 "home.html"
                 (merge {:messages (db/get-messages)}
                        (select-keys flash [:name :message :errors]))))

(defn save-message! [{:keys [params]}]
  (if-let [errors (validate-params params)]
    (-> (response/found "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/save-message! params)
      (response/found "/"))))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/message" {:post save-message!}]
   ["/about" {:get about-page}]])

