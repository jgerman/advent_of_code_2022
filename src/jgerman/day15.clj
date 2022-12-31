(ns jgerman.day15
  (:require [jgerman.utils :as utils]
            [instaparse.core :as insta]
            [clojure.math :as math]))


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

(defn get-corner [point d direction]
  (reduce (fn [m [k v]]
            (assoc m k (direction v d)))
          {}
          point))

(def ^:dynamic *bounded-y* nil)

(defn ->bounded-points
  "Takes a point, a manhattan distance d, determines all points that are d away.
  Essentially all of the points contained in a bounding box d away."
  [{:keys [x y] :as point} d]
  (let [upper-left (get-corner point d -)
        lower-right (get-corner point d +)
        y-range (if *bounded-y*
                  *bounded-y*
                  (range (:y upper-left) (inc (:y lower-right))))]
    (for [x (range (:x upper-left) (inc (:x lower-right)))
          y y-range]
      {:x x
       :y y})))

(defn ->within-distance [point d]
  (let [bounded-points (->bounded-points point d)]
    (filter (fn [p]
              (<= (manhattan-distance point p) d))
            bounded-points)))

(defn beacon-exclusion [{:keys [sensor beacon]}]
  (let [d (manhattan-distance sensor beacon)]
    (filter (fn [p]
              (not= p beacon))
            (->within-distance sensor d))))

(defn beacon-exclusions [data-points]
  (reduce (fn [s dp]
            (reduce conj s (beacon-exclusion dp)))
          #{}
          data-points))

(defn ->distances [{:keys [sensor beacon] :as data-point}]
  (assoc data-point :d (manhattan-distance sensor beacon)))

(defn remove-impossible
  "Filters out any data point that can't possibly effect a given row."
  [data-points row]
  (filter (fn [{:keys [sensor beacon]}]
            (let [d (manhattan-distance sensor beacon)]
              (and (<= row (+ (:y sensor) d))
                   (>= row (- (:y sensor) d)))))
          data-points))

(defn task-1 [resource row]
  (with-bindings {#'*bounded-y* [row]}
    (let [data-points (parse-input resource)
          potential-points (remove-impossible data-points row)
          exclusions (beacon-exclusions potential-points)]
      (count (filter (fn [p]
                       (= row (:y p)))
                     exclusions)))))

(comment
  (def sample-text (utils/resource->lines "day15/sample.txt"))

  (def parsed-sample (parse-input "day15/sample.txt"))
  (def parsed-input (parse-input "day15/input.txt"))
  (beacon-exclusion (first parsed-sample))
  (beacon-exclusion {:sensor {:x 8 :y 7} :beacon {:x 2 :y 10}})

  (beacon-exclusions parsed-sample)

  (= 26 (task-1 "day15/sample.txt" 10))
  (= 5461729  (task-1 "day15/input.txt" 2000000))


  ;;
  ,)
