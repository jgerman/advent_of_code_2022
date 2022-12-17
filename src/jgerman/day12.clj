(ns jgerman.day12
  (:require
   [jgerman.graph :as g]
   [jgerman.utils :as utils]))

(defn txfm-fn
  [c]
  (letfn [(get-val [c]
            (case c
              \S (int \a)
              \E (int \z)
              (int c)))]
   {:element (keyword (str c))
    :value (get-val c)}))

(defn neighbors-predicate [e1 e2]
  (<= (:value e2) (inc (:value e1))))

(defn find-elem [graph elem]
  (-> graph
      (g/find-element elem)))

(defn end? [node]
  (= :E (get-in node [:value :element])))

(defn find-start [graph] (first (find-elem graph :S)))
(defn find-lowest [graph]
  (conj (find-elem graph :a)
        (find-start graph)))

(defn shortest-path [graph start]
  (dec (count (g/bfs graph start end?))))

(defn task-1 [resource]
  (let [graph (-> resource
                  utils/resource->lines
                  (g/strings->graph :txfm-fn txfm-fn :neighbors-pred neighbors-predicate))]
    (shortest-path graph (find-start graph))
    #_(dec (count (g/bfs graph (find-start graph) end?)))))

;; probably could have done this cleaner, but (count nil) from shortest path is
;; going to return -1 because of the dec, everything is working though it's just
;; how I went about getting the paths
(defn task-2 [resource]
  (let [graph (-> resource
                  utils/resource->lines
                  (g/strings->graph :txfm-fn txfm-fn :neighbors-pred neighbors-predicate))
        lowest-points (find-lowest graph)]
    (apply min (filter (partial < 0) (map (partial shortest-path graph) lowest-points)))))

(comment
  (= 31 (task-1 "day12/sample.txt"))
  (= 425 (task-1 "day12/input.txt"))

  (= 29 (task-2 "day12/sample.txt"))
  (= 418 (task-2 "day12/input.txt"))


  ;;
  )
