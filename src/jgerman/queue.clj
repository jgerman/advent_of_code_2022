(ns jgerman.queue)

;; convenience for dealing with queues

(defmethod print-method clojure.lang.PersistentQueue [queue writer]
  (print-method ":tail -> " writer)
  (print-method (reverse (seq queue)) writer)
  (print-method " -> :head" writer))

(defn ->queue
  ([] clojure.lang.PersistentQueue/EMPTY)
  ([coll] (reduce conj (->queue) coll)))

(defn enqueue [queue element]
  (if (coll? element)
    (reduce conj queue element)
    (conj queue element)))

(defn dequeue [queue]
  (pop queue))

(comment
  (peek (->queue))

  (enqueue (->queue) :a)
  (enqueue (->queue) [:b [:c]])
  (enqueue (->queue) [[:b] [:c]])
  ;;
  )
