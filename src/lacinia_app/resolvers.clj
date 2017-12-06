(ns lacinia-app.resolvers
  (:require [integrant.core :as ig]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [clojure.edn :as edn]))

(defn resolve-game-by-id
  [games-map context args value]
  (let [{:keys [id]} args]
    (get games-map id)))

(defn resolve-board-game-designers
  [designers-map context args board-game]
  (->> board-game
       :designers
       (map designers-map)))

(defn resolve-designer-games
  [games-map context args designer]
  (let [{:keys [id]} designer]
    (->> games-map
         vals
         (filter #(-> % :designers (contains? id))))))

(defn entity-map
  [db k]
  (reduce #(assoc %1 (:id %2) %2)
          {}
          (get db k)))

(defmethod ig/init-key :lacinia-app/resolvers [_ {:keys [db]}]
  (let [games-map (entity-map db :games)
        designers-map (entity-map db :designers)]
    {:query/game-by-id (partial resolve-game-by-id games-map)
     :BoardGame/designers (partial resolve-board-game-designers designers-map)
     :Designer/games (partial resolve-designer-games games-map)}))
