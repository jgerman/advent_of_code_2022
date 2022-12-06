(ns jgerman.day6
  (:require [jgerman.utils :as utils]))

(defn all-different? [s]
  (= (count s) (count (distinct s))))

(defn start-of-packet [s window]
  (let [s' (partition window 1 s)]
    (loop [offset window
           s'' s']
      (if (all-different? (first s''))
        offset
        (recur (inc offset) (rest s''))))))

(defn task-1 []
  (-> "day6/input.txt"
      utils/resource->text
      (start-of-packet 4)))

(defn task-2 []
  (-> "day6/input.txt"
      utils/resource->text
      (start-of-packet 14)))

(comment
  (= 7 (start-of-packet "mjqjpqmgbljsphdztnvjfqwrcgsmlb" 4))
  (= 5 (start-of-packet "bvwbjplbgvbhsrlpgdmjqwftvncz" 4))
  (= 6 (start-of-packet "nppdvjthqldpwncqszvftbrmjlhg" 4))
  (= 10 (start-of-packet "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg" 4))
  (= 11 (start-of-packet "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw" 4))

  (= 1702 (task-1))
  (= 3559 (task-2))

;;
  )
