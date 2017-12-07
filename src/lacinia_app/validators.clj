(ns lacinia-app.validators)

(def url? (partial re-matches #"https?://[\w/:%#\$&\?\(\)~\.=\+\-]+"))
