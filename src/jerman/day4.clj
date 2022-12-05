(ns jerman.day4
  (:require [jgerman.utils :as utils]
            [clojure.string :as str]
            [clojure.set :as s]))

(defn pair->range [pair]
  (map utils/str->int
       (str/split pair #"-")))

(defn line->assignment-pairs [line]
  (map pair->range (str/split line #",")))

(defn prepare-input [resource]
  (->> resource
       utils/resource->lines
       (map line->assignment-pairs)))

(defn count-condition [input overlap-fn]
  (->> input
       (map overlap-fn)
       (filter identity)
       count))

(defn ->set [[start end]]
  (set (range start (inc end))))

(defn fully-overlap? [[p1 p2]]
  (let [set-1 (->set p1)
        set-2 (->set p2)]
    (or (empty? (s/difference set-1 set-2))
        (empty? (s/difference set-2 set-1)))))

(defn partially-overlap? [[p1 p2]]
  (let [set-1 (->set p1)
        set-2 (->set p2)]
    (not-empty (s/intersection set-1 set-2))))

(defn task-1 [resource]
  (count-condition (prepare-input resource) fully-overlap?))

(defn task-2 [resource]
  (count-condition (prepare-input resource) partially-overlap?))

(comment

  (def sample (prepare-input "day4/sample.txt"))

  (= 2 (task-1 "day4/sample.txt"))
  (= 485 (task-1 "day4/input.txt"))

  (= 4 (task-2 "day4/sample.txt"))
  (= 857 (task-2 "day4/input.txt"))
;;
  )
