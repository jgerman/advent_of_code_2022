(ns jgerman.day16
  (:require [instaparse.core :as insta]
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
  (apply + (map (fn [valve]
                  (:flow-rate (get-valve nodes valve)))
                open-valves)))

(declare get-child-states)

(defn process-state [nodes {:keys [location time-remaining open-valves parent released] :as current-state}]
  (let [valve-data (get-valve nodes location)
        potential-moves (filter (fn [t] (not= parent t)) (:tunnels valve-data))
        open?  (some #{location} open-valves)]
    (cond
      (= 0 time-remaining) (assoc current-state
                                  :children '())
      (not open?) (assoc current-state
                         :children
                         (conj (get-child-states nodes current-state potential-moves)
                               (process-state nodes (assoc current-state
                                                           :open-valves (conj open-valves location)
                                                           :released (+ released (open-pressure nodes (conj open-valves location)))
                                                           :time-remaining (dec time-remaining)))))
      :else
      (assoc current-state
             :children (get-child-states nodes current-state potential-moves)))))

(defn get-child-states [nodes {:keys [location time-remaining open-valves parent released] :as current-state} moves]
  (map (fn [child]
         (process-state nodes
                        (assoc current-state
                               :location child
                               :released (+ released (open-pressure nodes open-valves))
                               :time-remaining (dec time-remaining)
                               :parent location)))
       moves))
(defn build-tree [nodes start-node]
  (let [root {:location start-node
              :time-remaining 30
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
  ,)
