(ns jgerman.day2
  (:require [jgerman.utils :as utils]
            [clojure.java.io :as io]
            [clojure.string :as str]))


(def rock-paper-scissors [:paper :rock :scissors :paper])
(defn choice-value [choice]
  (case choice
    :rock 1
    :paper 2
    :scissors 3))

(defn winner
  "Returns :p1, p2, or :draw."
  [p1 p2]
  (let [p1-idx (.indexOf rock-paper-scissors p1)
        p1-win-scenario (subvec rock-paper-scissors
                                p1-idx
                                (+ 2 p1-idx))]
    (cond
      (= p1 p2) :draw
      (= p1-win-scenario [p1 p2]) :p1
      :else :p2)))

(defn symbol->kw-win [choice]
  (case choice
    ("A" "X") :rock
    ("B" "Y") :paper
    ("C" "Z") :scissors))

(defn ->line-win
  [s]
  (let [line (str/split s #" ")]
    (map symbol->kw-win line)))

(defn lose-to [play]
  (let [move (case play
               :rock :scissors
               :scissors :paper
               :paper :rock)]
    [play move]))

(defn beat [play]
  (let [move (case play
               :rock :paper
               :scissors :rock
               :paper :scissors)]
    [play move]))

(defn ->line-goal
  [s]
  (let [[move goal] (str/split s #" ")
        opponent (symbol->kw-win move)]
    (case goal
      "X" (lose-to opponent)
      "Y" [opponent opponent]
      "Z" (beat opponent))))

(defn prep-strategy-guide [resource strategy]
  (->> resource
       utils/resource->lines
       (map strategy)))

(defn round
  "Takes in the opponent's play, my play, and returns a score."
  [opponent me]
  (+ (choice-value me)
   (case (winner opponent me)
       :p2 6
       :draw 3
       :p1 0)))

(defn task-1 [resource]
  (let [strategy-guide (prep-strategy-guide resource ->line-win)]
    (reduce +
            (map (fn [[opp me]]
                   (round opp me))
                 strategy-guide))))

(defn task-2 [resource]
  (let [strategy-guide (prep-strategy-guide resource ->line-goal)]
    (reduce +
            (map (fn [[opp me]]
                   (round opp me))
                 strategy-guide))))

(comment
  (prep-strategy-guide "day2/sample.txt" ->line-win)
  (prep-strategy-guide "day2/sample.txt" ->line-goal)

  (round :rock :scissors)

  (= 15 (task-1 "day2/sample.txt"))
  (= 9241  (task-1 "day2/input.txt"))

  (= 12 (task-2 "day2/sample.txt"))
  (= 14610  (task-2 "day2/input.txt"))
  ;;
  )
