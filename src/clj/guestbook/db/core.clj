(ns guestbook.db.core
  (:require
   [java-time :refer [java-date]]
   [next.jdbc.date-time]
   [next.jdbc.result-set]
   [conman.core :as conman]
   [mount.core :refer [defstate]]
   [guestbook.config :refer [env]]
   [clojure.test :as t]))

(defstate ^:dynamic *db*
  :start (conman/connect! {:jdbc-url (env :database-url)})
  :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/queries.sql")

(defn sql-timestam->inst [t]
  (-> t
      (.toLocalDateTime)
      (.atZone (java.time.ZoneId/systemDefault))
      (java-date)))

(extend-protocol next.jdbc.result-set/ReadableColumn
  java.sql.Timestamp
  (read-column-by-label [^java.sql.Timestamp v _]
    (sql-timestam->inst v))
  (read-column-by-index [^java.sql.Timestamp v _2 _3]
    (sql-timestam->inst v))

  java.sql.Date
  (read-column-by-label [^java.sql.Date v _]
    (sql-timestam->inst v))
  (read-column-by-index [^java.sql.Date v _2 _3]
    (sql-timestam->inst v))

  java.sql.Time
  (read-column-by-label [^java.sql.Time v _]
    (sql-timestam->inst v))
  (read-column-by-index [^java.sql.Time v _2 _3]
    (sql-timestam->inst v)))
