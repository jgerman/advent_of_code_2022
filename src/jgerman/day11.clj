(ns jgerman.day11
  (:require [jgerman.utils :as utils]
            [jgerman.items :as items]
            [clojure.string :as str]
            [clojure.math :as math]))


(defn ->items [s]
  (let [[_ items] (str/split s #":")]
    (mapv items/->item
          (map utils/str->int(-> items
              (str/replace #" " "")
              (str/split #","))))))

#_(defn ->op [s]
  (let [xs (str/split s #" ")
        [v op] (reverse xs)]
    ;; I could handle this more generally, but since the only time "old" appears
    ;; in the sample or input in the value position is't a square, I'm just
    ;; going to handle that case
    (if (= v "old")
      (fn [x] (int (math/pow x 2.0)))
      (partial (resolve (symbol op)) (utils/str->int v)))))

(defn ->op [s]
  (let [xs (str/split s #" ")
        [v op] (reverse xs)]
    ;; I could handle this more generally, but since the only time "old" appears
    ;; in the sample or input in the value position is't a square, I'm just
    ;; going to handle that case
    (cond
      (= v "old") (partial items/->item-square)
      (= op "+")   (fn [itm] (items/->item-add itm (utils/str->int v)))
      (= op "*")   (fn [itm] (items/->item-mult itm (utils/str->int v))))))

(defn ->pred [p]
  (let [v (utils/str->int (last (str/split p #" ")))]
    (fn [x]
      (items/divisible? x v))))

(defn ->monkey-num [m]
  (-> m
      (str/split #" ")
      last
      utils/str->int))

(defn ->index [n]
  (-> n
      (str/replace #":" "")
      (str/split #" ")
      last
      utils/str->int))

(defn reduce-worry [itm]
  (int (/ itm 3)))

;; original for task 1
(defn inspect*
  "Monkey inspects item"
  [monkey item]
  (reduce-worry ((:op monkey) item)))

(defn inspect
  "Monkey inspects item"
  [monkey item]
  ((:op monkey) item))

(defn throw-to [monkey item]
  (if ((:predicate monkey) item)
    (:true monkey)
    (:false monkey)))

(defn catch [monkeys monkey item]
  (let [m (get monkeys monkey)]
    (assoc monkeys
           monkey (update m :items conj item))))

(defn turn [monkeys monkey]
  (loop [m (get monkeys monkey)
         ms monkeys #_(assoc monkeys monkey (update m :inspected-items + (count (:items m))))
         items (:items m)]
    (if (empty? items)
      (assoc ms monkey m)
      (let [to-inspect (first items)
            new-item (inspect m to-inspect)
            catcher (throw-to m new-item)]
        (recur
         (assoc m
                :items (vec (rest (:items m)))
                :inspected-items (inc (:inspected-items m)))
         (catch ms catcher new-item)
         (rest items))))))

(defn round [monkeys]
  (loop [m 0
         ms monkeys]
    (if (<= (count monkeys) m)
      ms
      (recur (inc m) (turn ms m)))))

(defn run-sim [monkeys iter]
  (loop [idx 0
         ms monkeys]
    (if (<= iter idx)
      ms
      (recur (inc idx) (round ms)))))

(defn ->monkey [xs]
  (let [[name
         items
         operation
         pred t f] xs]
    {:name name
     :index (->index name)
     :items (->items items)
     :op (->op operation)
     :predicate (->pred pred)
     :true (->monkey-num t)
     :false (->monkey-num f)
     :inspected-items 0}))


(defn prepare-monkeys [resource]
  (->> resource
       utils/resource->lines
       (partition-by #(= "" %))
       (filter #(< 1 (count %)))
       (map ->monkey)
       vec))

(defn task-1 [resource]
  (let [results (-> resource
                    prepare-monkeys
                    (run-sim 20))]
    (->> results
         (map :inspected-items)
         sort
         reverse
         (take 2)
         (apply *))))

(defn task-2 [resource sims]
  (let [results (-> resource
                    prepare-monkeys
                    (run-sim sims))]
    (->> results
         (map :inspected-items)
         sort
         reverse
         (take 2)
         (apply *))))


(comment
;; task-1 needs to be converted to the non int version of items these won't work
;; as they stand
  (= 10605 (task-1 "day11/sample.txt"))
  (= 182293 (task-1 "day11/input.txt"))

  (= 2713310158 (task-2 "day11/sample.txt" 10000))
  (= 54832778815 (task-2 "day11/input.txt" 10000))
;;
  )
