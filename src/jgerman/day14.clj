(ns jgerman.day14
  (:require
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [jgerman.utils :as utils]))

(defn ->path [line]
  (-> line
      (str/replace #" " "")
      (str/split #"->")))

(defn ->vecs [pair]
  (mapv utils/str->int (str/split pair #",")))

(defn path->vecs [path]
  (map ->vecs path))

(defn prepare-input [resource]
  (->> resource
       utils/resource->lines
       (map ->path)
       (map path->vecs)))

(defn get-xs [path]
  (map first path))

(defn get-ys [path]
  (map second path))

(defn get-max-x [paths]
  (let [xs (map get-xs paths)]
    (apply max (flatten xs))))

(defn get-max-y [paths]
  (let [ys (map get-ys paths)]
    (apply max (flatten ys))))

(defn make-grid [max-x max-y]
  (vec
   (repeat (inc max-y)
           (vec (repeat (inc max-x) ".")))))

(defn get-val
  "Given a grid (vector of vectors), get element at x y with 0,0 being top right."
  [grid x y]
  (-> grid
      (nth y)
      (nth x)))

(defn set-val
  [grid v x y]
  (assoc grid y
         (assoc (nth grid y)
                x
                v)))

(defn print-grid [grid]
  (doseq [y (range (count grid))]
    (println (str/join "" (nth grid y)))))

(defn prepare-range [c1 c2]
  (update (vec (sort (list c1 c2))) 1 inc))


(defn interpolate [[x1 y1] [x2 y2]]
  (if (= y1 y2)
    (for [x (apply range (prepare-range x1 x2))] [x y1])
    (for [y (apply range (prepare-range y1 y2))] [x1 y])))

(defn path->points [path]
  (loop [acc []
         p   path]
    (if (= 1 (count p))
      (conj acc (first p))
      (recur (apply conj acc (interpolate (first p) (second p)))
             (rest p)))))

(defn ->rocks [paths]
  (mapcat path->points paths))

(defn place-rocks [grid points]
  (reduce (fn [g p]
            (apply set-val g "#" p))
          grid
          points))

(defn shift-rocks-left [rocks shift-amount]
  (map (fn [[x y]]
         [(- x shift-amount) y])
       rocks))

(defn setup-map [rocks]
  (let [empty-grid (make-grid 525 525)]
    (place-rocks empty-grid rocks)))

(defn max-y [rocks]
  (apply max (map second rocks)))

(defn step-sand
  "Given current location: if moving straight down is possible do so. Otherwise if
  moving down and left is possible do so, otherwise if moving down and right is
  possible do so. otherwise we're at rest, return the current position."
  [grid [x y]]
  (let [down       [x (inc y)]
        down-left  [(dec x) (inc y)]
        down-right [(inc x) (inc y)]]
    #_(tap> {:x x
           :y y
           :down down
           :down-left down-left
           :down-right down-right
           :down-value (apply get-val grid down)
           :down-left-value (apply get-val grid down-left)
           :down-right-value (apply get-val grid down-right)})
    (cond
      (= "." (apply get-val grid down)) down
      (= "." (apply get-val grid down-left)) down-left
      (= "." (apply get-val grid down-right)) down-right
      :else [x y])))

(defn drop-grain [grid max-y start-location]
  #_(tap> {:grain-at start-location})
  (let [new-location (step-sand grid start-location)]
    (cond
      (= max-y (second new-location)) nil
      (= start-location new-location) (apply set-val grid "o" new-location)
      :else (drop-grain grid max-y new-location))))

(defn drop-sand [grid max-y start-location]
  (loop [i 0
         g grid]
    (let [new-grid (drop-grain g max-y start-location)]
      (if (nil? new-grid)
        i
        (recur (inc i) new-grid)))))

(defn task-1 [resource]
  (let [rocks (->rocks (prepare-input resource))
        max-y (max-y rocks)
        starting-map (setup-map rocks)]
    (-> starting-map
        (drop-sand max-y [500 0]))))

(comment
  (add-tap (bound-fn* pp/pprint))


  (= 24 (task-1 "day14/sample.txt"))
  (= 728 (task-1 "day14/input.txt"))



  ;;
  )
