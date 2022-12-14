(ns jgerman.day13
  (:require
   [jgerman.utils :as utils]
   [clojure.string :as str]))

(defn prepare-input [resource]
  (->> resource
       utils/resource->lines
       (filter not-empty)
       (map (fn [l]
              (-> l
                  (str/replace "[" "(")
                  (str/replace "]" ")"))))
       (map read-string)))

(defn prepare-task-1 [resource]
  (->> resource
       prepare-input
       (partition 2)))

(defn prepare-task-2 [resource]
  (-> resource
      prepare-input
      (conj '((2)))
      (conj '((6)))))

(defn coerce [v]
  (if (coll? v) v (vector v)))

(declare process-lists)

(defn handle-ints [left right]
  (cond
    (< left right) -1
    (< right left) 1
    :else 0))

(defn process-elements [left right]
  (cond
    (and (int? left)
         (int? right)) (handle-ints left right)
    :else (process-lists (coerce left) (coerce right))))

(defn process-lists [left right]
  (cond
    (and (empty? left)
         (empty? right)) 0
    (empty? left) -1
    (empty? right) 1
    :else (case (process-elements (first left) (first right))
            -1 -1
            1 1
            (process-lists (rest left) (rest right)))))

(defn task-1 [resource]
  (let [pairs (prepare-task-1 resource)]
    (->> pairs
         (map-indexed (fn [i x] [(inc i) (apply process-lists x)]))
         (filter (fn [[_ b]] (= -1 b)))
         (map first)
         (apply +))))

(defn task-2 [resource]
  (let [data (prepare-task-2 resource)]
    (let [sorted (->> data
                      (sort process-lists))]
      (* (inc (.indexOf sorted '((2))))
         (inc (.indexOf sorted '((6))))))))

(comment
  (= 13 (task-1 "day13/sample.txt"))
  (= 5506 (task-1 "day13/input.txt"))

  (= 140 (task-2 "day13/sample.txt"))
  (= 21756 (task-2 "day13/input.txt"))



  ;;
  )
