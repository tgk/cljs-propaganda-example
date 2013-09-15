(ns example
  (:use [propaganda.system :only [make-system add-value constant get-value stabile?]]
        [propaganda.values :only [default-merge default-contradictory?]]
        [propaganda.intervals.common :only [make-interval interval?]]
        [propaganda.intervals.system :only [extend-merge quadratic product]])
  (:require [clojure.string :as string]
            [example.components.Visualisation]))

(defn fall-duration
  [system t h]
  (let [g          (gensym)
        one-half   (gensym)
        t-squared  (gensym)
        gt-squared (gensym)]
    (-> system
        ((constant (make-interval 9.789 9.790)) g)
        ((constant 0.5) one-half)
        (quadratic t t-squared)
        (product g t-squared gt-squared)
        (product one-half gt-squared h))))

(defn similar-triangles
  [system s-ba h-ba s h]
  (let [ratio (gensym)]
    (-> system
        (product s-ba ratio h-ba)
        (product s ratio h))))

(defn system-chain
  [system]
  (when system
    (cons system
          (system-chain (-> system meta :prev)))))

(defn value-map
  [system]
  (let [values (:values system)
        ks (keys values)
        kw-keys (filter keyword? ks)]
    (select-keys values kw-keys)))

(defn pretty
  [v]
  (cond
   (interval? v) (format "[%.4f, %.4f]" (:lo v) (:hi v))
   :else (str v)))

(defn value-str
  [m]
  (string/join
   "\n"
   (for [[k v] (sort m)]
     (format "%20s %s" (name k) (pretty v)))))

(defn presentation-map
  [system]
  {:values (value-str (value-map system))
   :stabile (stabile? system)})

(defn building-height
  []
  (let [custom-merge (doto (default-merge) extend-merge)
        system (make-system custom-merge (default-contradictory?))
        audit-system (with-meta system {:audit? true})]
    (-> audit-system

        (fall-duration :fall-time :building-height)
        (add-value :fall-time (make-interval 2.9 3.1))

        (similar-triangles :barometer-shadow :barometer-height
                           :building-shadow :building-height)
        (add-value :building-shadow (make-interval 54.9 55.1))
        (add-value :barometer-height (make-interval 0.3 0.32))
        (add-value :barometer-shadow (make-interval 0.36 0.37))

        (add-value :building-height 45.0))))

(defn ^:export init
  []
  (let [result-system (building-height)
        chain (reverse (system-chain result-system))
        visualisation (example.components.Visualisation. "body" 960 300)]
    (.log js/console (clj->js (map presentation-map chain)))
    (.update visualisation (clj->js (map presentation-map chain)))

    ))
