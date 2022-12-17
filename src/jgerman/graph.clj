(ns jgerman.graph
  (:require
   [clojure.pprint :as pprint]
   [jgerman.queue :as queue]
   [clojure.set :as set]))


(defn strings->row [s & {:keys [txfm-fn]}]
  (let [xs (char-array s)
        transform (or txfm-fn (comp keyword str))]
    (mapv transform xs)))

(defn strings->grid [lines & {:keys [txfm-fn]}]
  (mapv (fn row-mapper [line]
          (strings->row line :txfm-fn txfm-fn))
        lines))

(defn get-val
  "Given a grid (vector of vectors), get element at x y with 0,0 being top right."
  [grid x y]
  (-> grid
      (nth y)
      (nth x)))

(defn neighbors
  "Provides a list of neighbors for an x, y position filtering out any that have
  a value that is < 0."
  [x y max-x max-y]
  (let [raw-neighbors [[x (inc y)]
                       [x (dec y)]
                       [(inc x) y]
                       [(dec x) y]]]
    (filter (fn [[x y]]
              (and (<= 0 x)
                   (< x max-x)
                   (<= 0 y)
                   (< y max-y))) raw-neighbors)))

(defn all-points [max-x max-y]
  (for [x (range max-x)
        y (range max-y)]
    [x y]))

(defrecord Node [value neighbors])

(defn max-x [grid]
  (count (first grid)))

(defn max-y [grid]
  (count grid))

(defn valid-neighbors [grid pred [x y]]
  (let [xs (neighbors x y (max-x grid) (max-y grid))]
    (filter #(pred (get-val grid x y)
                   (apply get-val grid %)) xs)))

(defn get-node [graph x y]
  (get graph [x y]))

(defn ->graph
  "Given a grid (which is just a vector of vectors) build an adjacency list using
  the predicate for each neighbor. If no pred-fn is provided use (constantly
  true). Assumes a regular grid (all x lengths are the same.)

  The returned adjacency list is returned as a map of pairs to elements:
  {[0 0] {:location [0 1] :val <grid val>}}."
  [grid & {:keys [pred-fn]}]
  (let [predicate (or pred-fn (constantly true))
        points (all-points (max-x grid) (max-y grid))]
    (reduce (fn [graph point]
              (assoc graph
                     point (Node. (apply get-val grid point)
                                  (valid-neighbors grid predicate point))))
            {}
            points)))

(defn find-element
  "For small graphs filter is fine, even if we go through all elements."
  [graph value]
  (filter #(= value (get-in (apply get-node graph %) [:value :element]))
          (keys graph)))

(defn strings->graph [strs & {:keys [txfm-fn neighbors-pred]}]
  (->graph (strings->grid strs :txfm-fn txfm-fn) :pred-fn neighbors-pred))

(defn backtrace [parents start end]
  (loop [path [end]
         n end]
    (let [parent (get parents n)]
      (if (= parent start)
        (reverse (conj path parent))
        (recur (conj path parent) parent)))))

(defn maintain-parents [parents k vs]
  (reduce (fn [ps ele]
            (assoc ps ele k))
          parents
          vs))

(defn bfs [graph start-element end-pred]
  (loop [queue (queue/->queue [start-element])
         visited #{}
         parents {}]
    (let [node (peek queue)]
      (cond
        (nil? node) nil
        (end-pred (apply get-node graph node)) (backtrace parents
                                                          start-element
                                                          node)
        :else
        (let [neighbors (:neighbors (apply get-node graph node))
              unvisited-neighbors (set/difference
                                   (set neighbors)
                                   (set/union visited (set queue)))]
          (recur (queue/enqueue
                  (queue/dequeue queue)
                  unvisited-neighbors)
                 (conj visited node)
                 (maintain-parents parents node unvisited-neighbors)))))))

(comment
  (add-tap (bound-fn* pprint/pprint))
  (strings->row "abcdeS" :txfm-fn (fn [element]
                                    {:element (keyword (str element))
                                     :value (int element)}))

  (strings->grid '("abc" "def" "Seg") :txfm-fn (fn e-v [element]
                                                 {:element (keyword (str element))
                                                  :value (int element)}))

  (def input (strings->grid '("abc" "def" "ghi")))

  (defn build-find-pred [elem]
    (fn [node]
      (= elem (:value node))))

  (def g (strings->graph '("abc" "def" "ghi")))
  (:neighbors (apply get-node g [0 0]))
  (bfs g [0 0] (build-find-pred :i))

;;
  )
