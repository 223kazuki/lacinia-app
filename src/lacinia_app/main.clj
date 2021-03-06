(ns lacinia-app.main
  (:gen-class)
  (:require [clojure.java.io :as io]
            [duct.core :as duct]))

(duct/load-hierarchy)

(defn -main [& args]
  (let [keys (or (duct/parse-keys args) [:lacinia-app/pedestal])]
    (-> (duct/read-config (io/resource "lacinia_app/config.edn"))
        (duct/prep keys)
        (duct/exec keys))))
