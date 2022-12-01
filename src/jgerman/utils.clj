(ns jgerman.utils
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]))


(defn resource->text [resource]
  (-> resource
      io/resource
      slurp))

(defn resource->edn [resource]
  (-> resource
      io/resource
      slurp
      read-string))

(defn resource->lines [resource]
  (-> resource
      io/resource
      slurp
      str/split-lines))

(defn str->int [s]
  (Integer/parseInt s))
