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
     (format "%17s %s" (name k) (pretty v)))))

(defn presentation-map
  [system]
  {:values (value-str (value-map system))
   :stabile (stabile? system)
   :step (or (-> system meta :step) "-")})

(defn add-meta
  [o k v]
  (with-meta o (assoc (meta o) k v)))

(defn index-first-map-with-f
  [ms f]
  (ffirst
   (remove (comp nil? second)
           (map-indexed (fn [i m] [i (f m)]) ms))))

(defn value-area
  [systems k]
  (when-let [offset (index-first-map-with-f systems (comp k :values))]
    {:offset offset
     :values (for [s (drop offset systems)]
               (let [v (-> s :values k)]
                 (if (interval? v) [(:lo v) (:hi v)]
                     [(- v 0.1) (+ v 0.1)])))}))

(defn building-height
  []
  (let [custom-merge (doto (default-merge) extend-merge)
        system (make-system custom-merge (default-contradictory?))
        audit-system (with-meta system {:audit? true})]
    (-> audit-system

        (add-meta :step :base)

        (add-meta :step :pre-fall-duration-relation)
        (fall-duration :fall-time :building-height)

        (add-meta :step :pre-fall-time)
        (add-value :fall-time (make-interval 2.9 3.1))

        (add-meta :step :pre-similar-triangles-relation)
        (similar-triangles :barometer-shadow :barometer-height
                           :building-shadow :building-height)

        (add-meta :step :pre-building-shadow)
        (add-value :building-shadow (make-interval 54.9 55.1))
        (add-meta :step :pre-barometer-height)
        (add-value :barometer-height (make-interval 0.3 0.32))
        (add-meta :step :pre-barometer-shadow)
        (add-value :barometer-shadow (make-interval 0.36 0.37))

        (add-meta :step :pre-building-height)
        (add-value :building-height 45.0))))

(defn ^:export init
  []
  (let [result-system (building-height)
        chain (reverse (system-chain result-system))
        visualisation (example.components.Visualisation. "body" 960 300)]
    (.update visualisation
             (clj->js (map presentation-map chain))
             (clj->js (value-area chain :barometer-shadow)))))
