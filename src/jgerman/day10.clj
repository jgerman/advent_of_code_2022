(ns jgerman.day10
  (:require [jgerman.utils :as utils]
            [clojure.string :as str]))

(defn parse-line [line]
  (let [[op v] (str/split line #" ")]
    (merge {:op (keyword op)}
           (when v
             {:val (utils/str->int v)}))))

(defn run [instructions]
  (loop [is instructions
         ip 1
         reg '({:x 1 :t 1})]
    (let [x (first is)
          xs (rest is)]
      (cond
        (nil? x) reg
        (= :noop (:op x)) (recur xs (inc ip) reg)
        (= :addx (:op x)) (recur xs (+ ip 2) (conj reg {:t (+ ip 2)
                                                        :x (+ (:x (first reg)) (:val x))}))))))

(defn val-at-ip [state-changes t]
  (:x (last (filter (fn [x]
                           (<= (:t x) t))
                         (reverse state-changes)))))
(defn ip->str
  "Take an instruction cycle, return the value of the register at that time. State
  changes come in reversed, most recent to least."
  [state-changes t]
  (* t (val-at-ip state-changes t)))

(defn task-1 [resource]
  (let [state-changes (->> resource
                           utils/resource->lines
                           (map parse-line)
                           run)
        get-val (partial ip->str state-changes)
        vals (map get-val [20 60 100 140 180 220])]
    (apply + vals)))

(defn mod-it [v]
  (if (< v 40)
    v
    (mod-it (mod v 40))))

(defn pixel-val [state-changes t]
  (let [center (val-at-ip state-changes (inc t))
        check (mod-it t)]
    (if (or (= check center)
            (= check (dec center))
            (= check (inc center)))
      "#"
      " ")))

(defn task-2 [resource]
  (let [state-changes (->> resource
                           utils/resource->lines
                           (map parse-line)
                           run)
        linear    (for [x (range 240)] (pixel-val state-changes x))
        lines     (partition 40 linear)]
    (println "-----------------------------------------------------------------------------")
    (doseq [l lines]
      (println (apply str l)))))

(comment
  (= 13140 (task-1 "day10/sample.txt"))
  (= 14780 (task-1 "day10/input.txt"))

  (task-2 "day10/sample.txt")
  (task-2 "day10/input.txt") ;; ELPLZGZL

  ,)
