(ns jgerman.day13
  (:require
   [clojure.pprint :as pprint]
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
       (map read-string)
       (partition 2)))

(defn coerce [v]
  (if (coll? v) v (vector v)))

(declare process-lists)

(defn handle-ints [left right]
  (cond
    (< left right) :lt
    (< right left) :gt
    :else :eq))

(defn process-elements [left right]
  (cond
    (and (int? left)
         (int? right)) (handle-ints left right)
    :else (process-lists (coerce left) (coerce right))))

(defn process-lists [left right]
  (cond
    (and (empty? left)
         (empty? right)) :eq
    (empty? left) :lt
    (empty? right) :gt
    :else (case (process-elements (first left) (first right))
            :lt :lt
            :gt :gt
            (process-lists (rest left) (rest right)))))

(defn task-1 [resource]
  (let [pairs (prepare-input resource)]
    (->> pairs
         (map-indexed (fn [i x] [(inc i) (apply process-lists x)]))
         (filter (fn [[_ b]] (= :lt b)))
         (map first)
         (apply +))))

(comment
  (add-tap (bound-fn* pprint/pprint))
  (def sample (prepare-input "day13/sample.txt"))
  (def input (prepare-input "day13/input.txt"))


  (= 13 (task-1 "day13/sample.txt"))
  (= 5506 (task-1 "day13/input.txt"))



  ;;
  )
