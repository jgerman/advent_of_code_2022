(ns jgerman.items
  (:require [clojure.math :as math]))


;; 3 11 19 5 2 17 7 13


(def primes [2 3 5 7 11 13 17 19 23])

(defn ->item
  "Turns a num into an item that tracks it's multiples of the first 8 primes."
  [num]
  (map (fn [prime]
         {:prime prime
          :rem  (mod num prime)})
       primes))

(defn get-factors [item num]
  (first (filter #(= num (:prime %)) item)))

(defn divisible? [item num]
  (let [factored (get-factors item num)]
    (= 0 (:rem factored))))

(defn factor-add [factor num]
  (let [remainder (mod (+ num (:rem factor)) (:prime factor))]
    (assoc factor :rem remainder)))

(defn factor-mult [factor num]
  (let [remainder (mod (* num (:rem factor)) (:prime factor))]
    (assoc factor :rem remainder)))

(defn factor-square [factor]
  (let [remainder (mod (int (math/pow (:rem factor) 2.0)) (:prime factor))]
    (assoc factor :rem remainder)))

(defn ->item-add [item num]
  (map (fn [f]
         (factor-add f num)) item))

(defn ->item-mult [item num]
  (map (fn [f]
         (factor-mult f num)) item))

(defn ->item-square [item]
  (map (fn [f]
         (factor-square f)) item))

(comment
  (def item (->item 10))


  (->item-add item 2)
  (->item-mult item 2)
  (->item-square (->item 121))

  (divisible? item 5)
  ;;
  )
