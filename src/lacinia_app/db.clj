(ns lacinia-app.db
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [clojure.edn :as edn]))

(defmethod ig/init-key :lacinia-app/db [_ {:keys [data-path]}]
  (-> (io/resource data-path)
      slurp
      edn/read-string))
