(ns lacinia-app.db
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [clojure.edn :as edn]))

(defmethod ig/init-key :lacinia-app/db [_ {:keys [initial-data-path]}]
  (-> (io/resource initial-data-path)
      slurp
      edn/read-string
      atom))

(defmethod ig/halt-key! :lacinia-app/db [_ db]
  (reset! db nil))
