#!/usr/bin/env bb

;; Depends on `sp` bash script, found here:
;; https://gist.github.com/streetturtle/fa6258f3ff7b17747ee3

(require '[clojure.string :as string])
(require '[clojure.java.shell :as sh])

(defn url->uri [url]
  (println url)
  (-> url
      (string/replace "https://open.spotify.com/" "spotify:")
      (string/replace "track/" "track:")
      (string/replace "album/" "album:")
      (string/replace "artist/" "artist:")
      (string/replace #"\?.*" "")))

(comment
  (url->uri "https://open.spotify.com/track/7oaEjLP2dTJLJsITbAxTOz")
  (url->uri "https://open.spotify.com/track/0oks4FnzhNp5QPTZtoet7c?si=9qdFwTT2Tm-54DNT9wWSbA")
  )

(defn open [uri]
  (sh/sh "sp" "open" uri))

(let [[url] *command-line-args*]
  (->
    (url->uri url)
    open))
