(ns jgerman.day16
  (:require
   [clojure.set :as s]
   [instaparse.core :as insta]
   [jgerman.utils :as utils]))


;; going with instaparse again even though it's overkill
(def map-grammar
  (insta/parser
   "<S> = valve <ws> flow-rate <ws> tunnels
    valve = <\"Valve\"> <ws> valve-label
    valve-label = letter+
    letter = #'[A-Z]'
    flow-rate = <\"has flow rate=\"> num <';'>
    tunnels = (<\"tunnels lead to valves\"> | <\"tunnel leads to valve\">) <ws> tunnel-list
    <tunnel-list> = valve-label | valve-label <','> <ws> tunnel-list
    ws = #'\\s*'
    num  = #'[0-9]+'"))

(def valve-label {:valve-label (fn [[_ l1] [_ l2]]
                                (str l1 l2))})

(def num-txfm {:num utils/str->int})

(def tunnels-txfm {:tunnels (fn tunnel-list [& xs] [:tunnels (apply list xs)])})

(def map-txfm {:tunnels (fn [x] {:tunnels x})
               :flow-rate (fn [x] {:flow-rate x})
               :valve (fn [x] {:valve x})})

(defn parse-line [l]
  (->> l
       map-grammar
       (insta/transform valve-label)
       (insta/transform num-txfm)
       (insta/transform tunnels-txfm)
       (insta/transform map-txfm)
       (apply merge)))

(defn parse-input [resource]
  (map parse-line (utils/resource->lines resource)))

(defn get-valve [nodes valve]
  (first (filter #(= valve (:valve %)) nodes)))

(def state {:location "AA"
            :time-remaining 30
            :open-valves []
            :children []})


(defn open-pressure [nodes open-valves]
  #_(tap> {:nodes
         :open-valves})
  (apply + (map (fn [valve]
                  (:flow-rate (get-valve nodes valve)))
                open-valves)))

(defn all-open? [nodes open-valves]
  (= (open-pressure nodes open-valves)
     (apply + (map :flow-rate nodes))))

;; attempt to add heuristics to prune the tree first just looking at each child
;; and prioritizing those with the highest non-zero closed valve and assuming
;; that's always the right move
(defn ->potential-moves
  [nodes location open-valves]
  (let [valve-data (get-valve nodes location)
        default-children (:tunnels valve-data)
        children (->> default-children
                      (map (partial get-valve nodes))
                      (sort-by :flow-rate)
                      reverse)
        open-children (->> children
                           (filter (fn [open-valve]
                                     (and (not (some #{(:valve open-valve)} open-valves))
                                          (< 0 (:flow-rate open-valve))))))]
    ;; if there are any open children take the biggest
    (if (< 0 (count open-children))
      (list (:valve (first open-children)))
      default-children)))

(defn larger-than-unopened-neighbors? [nodes location open-valves]
  (let [valve-data (get-valve nodes location)
        neighbors (:tunnels valve-data)
        unopened-neighbors (s/difference (set neighbors) open-valves)
        unopened-flows (map (fn [valve-label]
                              (:flow-rate (get-valve nodes valve-label))) unopened-neighbors)]
    (every? (partial >=(:flow-rate valve-data)) unopened-flows)))

(declare get-child-states)

(defn process-state [nodes {:keys [location open-valves just-opened? parent step released] :as current-state}]
  (let [valve-data (get-valve nodes location)
        potential-moves (->potential-moves nodes location open-valves)
        open?  (some #{location} open-valves)]
    (cond
      (all-open? nodes open-valves) (assoc current-state
                                           :children '()
                                           :released (+ released (* (open-pressure nodes open-valves)
                                                                    (- 30 step))))

      (= 30 step) (assoc current-state
                         :released (+ released (open-pressure nodes open-valves))
                         :children '())
      (and (not open?)
           (larger-than-unopened-neighbors? nodes location open-valves))
      (assoc current-state
             :children
             (conj '()
                   (process-state nodes (assoc current-state
                                               :open-valves (conj open-valves location)
                                               :pressure (open-pressure nodes (conj open-valves location))
                                               :just-opened? true
                                               :released (+ released (open-pressure nodes (conj open-valves location)))
                                               :step (inc step)))))
      :else
      (assoc current-state
             :children (get-child-states nodes current-state potential-moves)))))

(defn get-child-states [nodes {:keys [location time-remaining open-valves step released] :as current-state} moves]
  (map (fn [child]
         (process-state nodes
                        (assoc current-state
                               :location child
                               :released (+ released (open-pressure nodes open-valves))
                               :just-opened? false
                               :pressure (open-pressure nodes open-valves)
                               :step (inc step)
                               :parent location)))
       moves))
(defn build-tree [nodes start-node]
  (let [root {:location start-node
              :step 1
              :pressure 0
              :released 0
              :open-valves #{}}]
    (process-state nodes root)))

(comment
  (def sample (parse-input "day16/sample.txt"))

  (get-valve sample "AA")

  (conj '() nil)

  (def tree (build-tree sample "AA"))

  (def nodes (tree-seq (constantly true) :children tree))

  (first nodes)

  (count nodes)

  (apply max (map :released nodes))

  (not (some #{"DD"} #{"DD" "AA"}))

  (open-pressure sample #{"DD" "BB"})


  (->potential-moves sample "DD" #{"DD"})

  (larger-than-unopened-neighbors? sample "AA" #{"II" "BB"})
  (s/difference #{"DD" "II" "BB"} #{"DD" "II" "BB"})
  (all-open? sample #{"DD" "BB" "CC" "EE" "HH" "JJ"})



  ;;
  ,)
