(ns jgerman.day9
  (:require
   [clojure.math :as math]
   [clojure.string :as str]
   [jgerman.utils :as utils]))


(defn parse-line [line]
  (let [[dir dist] (str/split line #" ")]
    [dir (utils/str->int dist)]))

(defn expand-line [[dir dist]]
  (repeat dist [dir 1]))

(defn expand-input [lines]
  (mapcat (comp expand-line parse-line) lines))

(defn rope-distance [[x1 y1] [x2 y2]]
  (int (math/floor (math/sqrt (+ (math/pow (- x1 x2) 2.0)
                                 (math/pow (- y1 y2) 2.0))))))

(defn adjacent? [h t]
  (< (rope-distance h t) 2))

(defn move-head [[x y] [dir _]]
  (case dir
    nil nil
    "R" [(inc x) y]
    "L" [(dec x) y]
    "U" [x (inc y)]
    "D" [x (dec y)]))

(defn towards [h t]
  (cond
    (= h t) t
    (< h t) (dec t)
    (< t h) (inc t)))

(defn chase-head [[head-x head-y] [tail-x tail-y]]
  (if (or (nil? head-x)
          (adjacent? [head-x head-y] [tail-x tail-y]))
    [tail-x tail-y]
    [(towards head-x tail-x) (towards head-y tail-y)]))

(defn get-visited [lines]
  (loop [visited #{}
         head-loc [0 0]
         tail-loc [0 0]
         moves lines]
    (let [move (first moves)
          new-head (move-head head-loc move)
          new-tail (chase-head new-head tail-loc)]
      (if (nil? new-head)
        visited
        (recur (conj visited new-tail) new-head new-tail (rest moves))))))

(defn task-1 [resource]
  (let [input (-> resource utils/resource->lines expand-input)]
    (count (get-visited input))))

(comment
  (def sample (-> "day9/sample.txt"
                  utils/resource->lines))

  (def input (-> "day9/input.txt"
                 utils/resource->lines))

  (adjacent? [0 0] [0 0])
  (conj #{[0 0]} [0 0])

  (= 13 (task-1 "day9/sample.txt"))
  (= 6464 (task-1 "day9/input.txt"))
  ;;
  )
