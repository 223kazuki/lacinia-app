(ns lacinia-app.pedestal
  (:require [io.pedestal.http :as http]
            [com.walmartlabs.lacinia.pedestal :as pedestal]
            [integrant.core :as ig]))

(defmethod ig/init-key :lacinia-app/pedestal [_ {:keys [schema]}]
  (let [service (pedestal/service-map schema {:graphiql true})
        server (atom (http/create-server service))]
    (http/start @server)
    server))

(defmethod ig/halt-key! :lacinia-app/pedestal [_ server]
  (http/stop @server))
