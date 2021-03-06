#!/usr/bin/env bb

;; Spotify dbus usage inspired by `sp` bash script, found here:
;; https://gist.github.com/streetturtle/fa6258f3ff7b17747ee3

;; Requires `gtk3` (for `gtk-launch`)

(require '[clojure.string :as string])
(require '[clojure.java.shell :as sh])

(defn is-spotify-url? [url]
  (-> url (string/includes? "https://open.spotify.com")))

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
  (url->uri "https://open.spotify.com/track/0oks4FnzhNp5QPTZtoet7c?si=9qdFwTT2Tm-54DNT9wWSbA"))

(defn sp-dbus [dbus-cmd arg]
  (let [sp-dest "org.mpris.MediaPlayer2.spotify"
        sp-path "/org/mpris/MediaPlayer2"
        sp-memb "org.mpris.MediaPlayer2.Player"]
    (sh/sh "dbus-send"
           "--print-reply"
           (str "--dest=" sp-dest)
           sp-path
           (str sp-memb "." dbus-cmd)
           arg)))

(defn open-spotify-uri [uri]
  (sp-dbus "OpenUri" (str "string:" uri)))

(defn open-other-url
  "Passes the desktop entry and args to `/usr/bin/gtk-launch`.
  TODO handle disowning the process after launching from a shell.
  "
  [browser-desktop-entry url]
  (println "Opening url via" browser-desktop-entry)
  (sh/sh "/usr/bin/gtk-launch" browser-desktop-entry url))

(let [url           (-> *command-line-args* first)
      browser-entry (-> *command-line-args* second)]
  (cond
    (is-spotify-url? url)
    (->
      (url->uri url)
      open-spotify-uri)

    browser-entry
    (open-other-url browser-entry url)))
