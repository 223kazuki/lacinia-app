(ns lacinia-app.pedestal
  (:require [integrant.core :as ig]
            [com.walmartlabs.lacinia.pedestal :as pedestal]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.cors :refer [allow-origin]]))

(defmethod ig/init-key :lacinia-app/pedestal [_ {:keys [schema graphiql]}]
  (let [options {:graphiql (true? graphiql)}
        preflight-route ["/graphql" :options (fn [_] {:status 200}) :route-name ::preflight]
        routes (as-> (pedestal/graphql-routes schema options) $
                 (conj $ preflight-route)
                 (route/expand-routes $)
                 (map (fn [route] (update-in route [:interceptors] (partial cons (allow-origin {:creds true :allowed-origins some?})))) $))
        service (pedestal/service-map schema (assoc options :routes routes))
        server (atom (http/create-server service))]
    (http/start @server)
    server))

(defmethod ig/halt-key! :lacinia-app/pedestal [_ server]
  (http/stop @server))
