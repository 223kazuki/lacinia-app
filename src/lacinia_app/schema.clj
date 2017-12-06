(ns lacinia-app.schema
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]))

(defmethod ig/init-key :lacinia-app/schema [_ {:keys [schema-path resolvers]}]
  (-> (io/resource schema-path)
      slurp
      edn/read-string
      (util/attach-resolvers resolvers)
      schema/compile))
