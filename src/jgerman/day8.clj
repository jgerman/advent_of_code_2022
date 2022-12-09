(ns jgerman.day8
  (:require [jgerman.utils :as utils]
            [clojure.string :as str]))

(defn print-grid [xs]
  (doseq [ls xs]
    (doseq [i ls]
      (print i))
    (println)))

(defn int-ify [line]
  (map utils/str->int (str/split line #"")))

(defn transpose* [xs]
  (apply map list xs))

(def transpose (memoize transpose*))

(defn prep-input [resource]
  (->> resource
       utils/resource->lines
       (map int-ify)))

(defn visible? [ls n]
  (let [v (nth ls n)
        [before after] ((juxt (partial take n)
                              (partial drop (inc n))) ls)]
    (or (every? #(> v %) before )
        (every? #(> v %) after))))

(defn count-visible [m]
  (let [transposed (transpose m)
        max-x (count (first m))
        max-y (count (first transposed))]
    (count (filter identity
            (for [x (range max-x)
                  y (range max-y)]
              (or (visible? (nth m y) x)
                  (visible? (nth transposed x) y)))))))

(defn get-view [coll height]
  (loop [acc '()
         c coll]
    (cond
      (empty? c) acc
      (= (first c) height) (cons (first c) acc)
      (> (first c) height) (cons (first c) acc)
      (< (first c) height) (recur (cons (first c) acc) (rest c)))))

(defn score
  "Really the same as visible? but we want to reverse the lists so we're counting
  'out'."
  [ls n]
  (let [v (nth ls n)
        [before after] ((juxt (partial take n)
                              (partial drop (inc n))) ls)]
    (* (count (get-view (reverse before) v))
       (count (get-view after v)))))

(defn get-scores [m]
  (let [transposed (transpose m)
        max-x (count (first m))
        max-y (count (first transposed))]
    (for [x (range max-x)
          y (range max-y)]
      (* (score (nth m y) x)
         (score (nth transposed x) y)))))

(defn task-1 [resource]
  (let [m (prep-input resource)]
    (count-visible m)))

(defn task-2 [resource]
  (let [m (prep-input resource)]
    (apply max (get-scores m))))


(comment
  (= 21 (task-1 "day8/sample.txt"))
  (= 1805  (task-1 "day8/input.txt"))

  (= 8 (task-2 "day8/sample.txt"))
  (= 444528 (task-2 "day8/input.txt"))

  ;;
  )
