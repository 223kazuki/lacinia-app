(ns lacinia-app.umlaut
  (:require [integrant.core :as ig]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [umlaut.generators.lacinia :as lacinia]
            [umlaut.generators.dot :as dot]
            [umlaut.generators.spec :as spec]
            [umlaut.generators.graphql :as graphql]
            [umlaut.core :as core]
            [umlaut.utils :as utils]
            [clojure.pprint :refer [pprint]]))

(defn- join-path [out filename]
  "Join two paths"
  (-> out
      (io/file filename)
      (.getPath)))

(defn- get-umlaut-files [in]
  "Returns a list of .umlaut files from input folder"
  (->> in
       (io/file)
       (file-seq)
       (map #(.getPath %))
       (filter #(str/ends-with? % ".umlaut"))))

(defn- process-dot [ins out]
  (let [umlaut (core/main ins)]
    (utils/save-dotstring-to-image (join-path out "all.png") (dot/gen-all umlaut))
    (utils/save-string-to-file (join-path out "all.dot") (dot/gen-all umlaut))
    (reduce (fn [acc [key value]]
              (utils/save-dotstring-to-image (join-path out (str key ".png")) value)
              (utils/save-string-to-file (join-path out (str key ".dot")) value))
            {} (seq (dot/gen-by-group umlaut)))))

(defn- process-spec [ins args]
  (let [out (first args)
        spec-package (second args)
        custom-validators-filepath (nth args 2)
        id-namespace (nth args 3)
        specs (spec/gen spec-package custom-validators-filepath id-namespace ins)]
    (doseq [[k v] specs]
      (let [filename (clojure.string/replace k #"-" "_")]
        (utils/save-string-to-file (join-path out (str filename ".clj")) v)))))

(defn- process-lacinia [ins out]
  (utils/save-map-to-file out (lacinia/gen ins)))

(defn- process-graphql [ins out]
  (utils/save-string-to-file out (graphql/gen ins)))

(defmethod ig/init-key :lacinia-app/umlaut [_ {:keys [umlaut-files-folder
                                                      dot lacinia graphql spec]}]
  (let [umlaut-files (get-umlaut-files umlaut-files-folder)]
    (as-> {} $
      (if-let [{:keys [output-folder]} dot]
        (do (process-dot umlaut-files output-folder)
            (assoc $ :dot dot)) $)
      (if-let [{:keys [output-file]} lacinia]
        (do (process-lacinia umlaut-files output-file)
            (assoc $ :lacinia lacinia)) $)
      (if-let [{:keys [output-file]} graphql]
        (do (process-graphql umlaut-files output-file)
            (assoc $ :graphql graphql)) $)
      (if-let [{:keys [output-folder spec-package custom-validators-filepath id-namespace]} spec]
        (do (process-spec umlaut-files [output-folder spec-package custom-validators-filepath id-namespace])
            (assoc $ :spec spec)) $))))
