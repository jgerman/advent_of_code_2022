(ns jgerman.day3
  (:require [jgerman.utils :as utils]
            [clojure.set :as s]))

(defn ->priority
  "We'll assume valid letters are provided."
  [c]
  (let [val (int c)]
    (if (< 90 val)
      (- val 96)
      (- val 38))))

(defn to-compartments [line]
  (let [half (/ (count line) 2)]
    ((juxt (partial take half)
           (partial drop half))
     line)))

(defn shared-element [rucksacks]
  (first (apply s/intersection rucksacks)))

(defn group->sets [g]
  (map set g))

(defn find-shared [resource partition-fn]
  (->> resource
       utils/resource->lines
       partition-fn
       (map group->sets)
       (map shared-element)
       (map ->priority)
       (reduce +)))

(defn task-1 [resource]
  (find-shared resource (fn [xs]
                          (map to-compartments xs))))

(defn task-2 [resource]
  (find-shared resource (fn [xs]
                          (partition 3 xs))))

(comment
  (= 157 (task-1 "day3/sample.txt"))
  (= 8401 (task-1 "day3/input.txt"))

  (= 70 (task-2 "day3/sample.txt"))
  (= 2641  (task-2 "day3/input.txt"))

;;
  )
