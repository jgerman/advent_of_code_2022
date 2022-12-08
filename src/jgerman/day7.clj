(ns jgerman.day7
  (:require [jgerman.utils :as utils]
            [clojure.string :as str]))

(def total-space 70000000)
(def needed-space 30000000)

(defn process-cd [{:keys [stack dirs] :as state} line]
  (let [[_ _ dir] (str/split line #" ")]
    (if (= dir "..")
      (assoc state :stack (rest stack))
      (assoc state :stack (conj stack (str (keyword dir) "-" (java.util.UUID/randomUUID)))))))

(defn process-file [{:keys [dirs stack] :as state} line]
  (let [[size-str _] (str/split line #" ")
        size (utils/str->int size-str)
        #_#_curr-size (or (get dirs curr-dir) 0)]
    (assoc state
           :dirs (reduce (fn [dirs directory]
                           (assoc dirs directory (+ size (or (get dirs directory) 0))))
                         dirs
                         stack))))

(defn process-line [state line]
  (cond
    (str/starts-with? line "$ cd") (process-cd state line)
    (str/starts-with?  line "$ ls") state
    (str/starts-with? line "dir") state
    :else (process-file state line)))

(defn simple-dir-sizes [lines]
  (reduce process-line
          {:stack '()
           :dirs {}}
          lines))

(defn task-1 [resource]
  (->> resource
       utils/resource->lines
       simple-dir-sizes
       :dirs
       vals
       (filter #(<= % 100000))
       (reduce +)))

(defn space-used
  "We could search for root directly but root has to be the largest. Plus if it's
  tricky and there are two '/' dirs this way is foolproof."
  [sizes]
  (apply max (vals sizes)))

(defn task-2 [resource]
  (let [sizes (->> resource
                   utils/resource->lines
                   simple-dir-sizes
                   :dirs)
        used (space-used sizes)
        empty-space (- total-space used)
        space-needed (- needed-space empty-space)]
    (apply min (filter (fn [s]
                         (<= space-needed s))
                       (vals sizes)))))

(comment

  (= 95437 (task-1 "day7/sample.txt"))
  (= 1749646 (task-1 "day7/input.txt"))

  (= 1498966(task-2 "day7/input.txt"))

;;
  )
