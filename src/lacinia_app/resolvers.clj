(ns lacinia-app.resolvers
  (:require [integrant.core :as ig]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [clojure.edn :as edn]))

(defn entity-map
  [db k]
  (reduce #(assoc %1 (:id %2) %2)
          {}
          (get @db k)))

(defn resolve-game-by-id
  [db context args value]
  (let [games-map (entity-map db :games)
        {:keys [id]} args]
    (get games-map id)))

(defn resolve-board-game-designers
  [db context args board-game]
  (let [designers-map (entity-map db :designers)]
    (->> board-game
         :designers
         (map designers-map))))

(defn resolve-designer-games
  [db context args designer]
  (let [games-map (entity-map db :games)
        {:keys [id]} designer]
    (->> games-map
         vals
         (filter #(-> % :designers (contains? id))))))

(defn resolve-add-game!
  [db context args value]
  (let [next-id (or (some->> (:games @db)
                             (map :id)
                             (map #(Integer. %))
                             sort
                             last
                             inc
                             str)
                    "0000")
        game (-> args
                 (select-keys [:name :summary :description :designers
                               :min_players :max_players :play_time])
                 (assoc :id next-id))]
    (swap! db update-in [:games] #(conj % game))
    game))

(defmethod ig/init-key :lacinia-app/resolvers [_ {:keys [db]}]
  ;; TODO: https://github.com/workco/umlaut/issues/40
  {:query_game-by-id (partial resolve-game-by-id db)
   :BoardGame_designers (partial resolve-board-game-designers db)
   :Designer_games (partial resolve-designer-games db)
   :mutaition_add-game! (partial resolve-add-game! db)})
