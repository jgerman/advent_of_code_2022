(ns jgerman.day1
  (:require
   [jgerman.utils :as utils]))


(defn prepare-input [resource]
    (->> resource
       utils/resource->lines
       (partition-by #(= "" %))
       (filter #(not= '("") %))
       (map
        #(map utils/str->int %))))

(defn strings->int [xs]
  (map utils/str->int xs))

(defn sum-elves [elves]
  (map #(reduce + %) elves))

(defn task1 [resource]
  (->> resource
       prepare-input
       sum-elves
       (sort >)
       first))

(defn task2 [resource]
  (->> resource
       prepare-input
       sum-elves
       (sort >)
       (take 3)
       (reduce +)))

(comment

  (= 24000  (task1 "day1/sample.txt"))
  (= 69795  (task1 "day1/input.txt"))
  (= 208437 (task2 "day1/input.txt"))
  ;;
  )
