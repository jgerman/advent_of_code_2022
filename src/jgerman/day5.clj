(ns jgerman.day5
  (:require [jgerman.utils :as utils]
            [clojure.string :as str]))

(defn sep-input
  "Break the input into the initial state and the instructions."
  [lines]
  (let [pivot (.indexOf lines "")]
    [(take pivot lines)
     (drop (inc pivot) lines)]))

(defn longest [lines]
  (apply max (map count lines)))

(defn pad-right [desired-length s]
  (let [spaces (- desired-length (count s))]
    (str s
         (apply str (repeat spaces " ")))))

(defn normalize [lines]
  (let [l (longest lines)]
    (map (partial pad-right l) lines)))

(defn space? [c]
  (= \space c))

(defn not-space? [c]
  (not= \space c))

(defn prepare-state [initial-state]
  (->> initial-state
       normalize
       (apply map list)
       (filter (fn [l] (not-space? (last l))))
       (map #(drop-while space? %))
       vec))

(defn instruction->tuple [instr]
  (let [xs (str/split instr #" ")]
    (map utils/str->int [(get xs 1)
                         (get xs 3)
                         (get xs 5)])))

(defn pop-n
  ([xs] (pop-n xs 1))
  ([xs n] [(take n xs) (drop n xs)]))

(defn push
  [xs v]
  (if (seq? v)
    (concat v xs)
    (conj xs v)))

(defn move-val
  "Takes a state (a vector of lists), a from index, a to index (both 1-based) and
  returns a new state, after popping an element from from, and pushing it to
  to."
  [state from to n]
  (let [from-idx (dec from)
        to-idx (dec to)
        [elements remaining] (pop-n (get state from-idx) n)]
    (assoc state
           from-idx remaining
           to-idx   (push (get state to-idx) elements))))

(defn crate-mover-9000 [state instr]
  (let [[num from to] (instruction->tuple instr)]
    (loop [s state
           n num]
      (if (= 0 n)
        s
        (recur (move-val s from to 1) (dec n))))))

(defn crate-mover-9001 [state instr]
  (let [[num from to] (instruction->tuple instr)]
    (move-val state from to num)))

(defn run-procedure [state instructions crate-mover]
  (reduce (fn [state instr]
            (crate-mover state instr))
          state
          instructions))

(defn task* [resource crate-mover]
  (let [[raw-state instructions] (-> resource
                                     utils/resource->lines
                                     sep-input)
        state (prepare-state raw-state)]
    (->> (run-procedure state instructions crate-mover)
         (map first)
         (apply str))))

(defn task-1 [resource]
  (task* resource crate-mover-9000))

(defn task-2 [resource]
  (task* resource crate-mover-9001))

(comment
  (= "CMZ" (task-1 "day5/sample.txt"))
  (= "VRWBSFZWM" (task-1 "day5/input.txt"))

  (= "MCD" (task-2 "day5/sample.txt"))
  (= "RBTWJWMCF" (task-2 "day5/input.txt"))
;;
  )
