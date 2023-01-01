(ns jgerman.day15
  (:require [jgerman.utils :as utils]
            [instaparse.core :as insta]))

;; total overkill but it's been so long since I used instaparse and it's so
;; valuable when I need it I wanted a refresher
(def sensor-grammar
  (insta/parser
   "<S> = sensor <colon> <ws> beacon
    sensor = <\"Sensor at\"> loc
    beacon = <\"closest beacon is at\"> loc
    <loc>    = <ws> coords
    <coords> = coord <comma> <ws> coord
    coord  = (x | y) <eq> num
    x      = 'x'
    y      = 'y'
    ws = #'\\s*'
    comma = ','
    eq   = '='
    colon = ':'
    num  = #'[-?0-9]+'"))

(def coord-txfm {:coord (fn [var val] {(first var)
                                       (utils/str->int (second val))})})

(def element-txfm {:sensor (fn [x y] {:sensor (merge x y)})
                   :beacon (fn [x y] {:beacon (merge x y)})})

;; clj-kondo is claiming unresolved var on those insta calls, which is nonsense
(defn parse-line [line]
  (->> line
       sensor-grammar
       (insta/transform coord-txfm)
       (insta/transform element-txfm)
       (apply merge)))

(defn parse-input [resource]
  (map parse-line (utils/resource->lines resource)))

(defn manhattan-distance [p1 p2]
  (let [[x1 y1] ((juxt :x :y) p1)
        [x2 y2] ((juxt :x :y) p2)]
    (+(abs (- x1 x2))
      (abs (- y1 y2)))))

(defn add-distance [{:keys [sensor beacon] :as data-point}]
  (assoc data-point :d (manhattan-distance sensor beacon)))

(defn x-range
  "Given a data point (sensor, beacon, distance) and a y value, what is the range
  of xs?"
  [y {:keys [sensor d]}]
  (let [distance-from-center (abs (- y (:y sensor)))]
    (if (< d distance-from-center)
      nil
      [(- (:x sensor) (- d distance-from-center))
       (+ (:x sensor) (- d distance-from-center))])))

(defn x-ranges
  "For a set of data points, what are the ranges of x at row y?"
  [y data-points]
  (filter identity (map (partial x-range y) data-points)))

(defn ->discrete-xs [x-range]
  (apply range (update x-range 1 inc)))

(defn data-points->discrete-xs [x-ranges]
  (apply concat (map ->discrete-xs x-ranges)))

(defn beacons-at-y [y data-points]
  (->> data-points
       (map :beacon)
       (map :y)
       (filter #(= % y))
       distinct
       count))

(defn overlap?
  "This expects pairs ordered by x."
  [[_ e1] [s2 _]]
  (<= s2 (inc e1)))

(defn merge-range [[s1 e1] [s2 e2]]
  [s1 (max e1 e2)])

(defn merge-ranges [ranges]
  (loop [stack '()
         rs (sort ranges)]
    (cond
      (nil? (first rs)) stack
      (empty? stack) (recur (conj stack (first rs)) (rest rs))
      (not (overlap? (first stack) (first rs))) (recur (conj stack (first rs)) (rest rs))
      :else (recur (conj (rest stack) (merge-range (first stack) (first rs)))
                   (rest rs)))))

(defn task-1 [resource row]
  (let [data-points (map add-distance (parse-input resource))
        points (into #{} (data-points->discrete-xs (x-ranges row data-points)))
        beacons (beacons-at-y row data-points)]
    (- (count points) beacons)))

(defn find-missing [[x1 x2] [x3 x4]]
  (inc (second (sort (list x1 x2 x3 x4)))))

(defn task-2 [resource max-y]
  (let [data-points (map add-distance (parse-input resource))
        ranges (map (fn [row]
                      {:y row :range (merge-ranges (x-ranges row data-points))}) (range 0 max-y))
        beacon-row (first (filter #(< 1 (count (:range %))) ranges))
        y-val (:y beacon-row)
        x-val (apply find-missing (:range beacon-row))]
    (+ y-val (* x-val 4000000))))

(comment
  ;; still not super fast for the actual input but good enough
  ;; and for task 2 we won't take the discrete point approach
  (= 26 (task-1 "day15/sample.txt" 10))
  (= 5461729  (task-1 "day15/input.txt" 2000000))

  (= 56000011 (task-2 "day15/sample.txt" 20))
  (= 10621647166538 (task-2 "day15/input.txt" 4000000))
  ;;
  ,)
