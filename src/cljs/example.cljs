(ns example
  (:use [propaganda.system :only [make-system add-value constant get-value]]
        [propaganda.values :only [default-merge default-contradictory?]]
        [propaganda.intervals.common :only [make-interval]]
        [propaganda.intervals.system :only [extend-merge quadratic product]]))

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

(defn test
  []
  (let [custom-merge (doto (default-merge) extend-merge)
        system (make-system custom-merge (default-contradictory?))
        result-system (-> system

                          (fall-duration :fall-time :building-height)
                          (add-value :fall-time (make-interval 2.9 3.1))

                          (similar-triangles :barometer-shadow :barometer-height
                                             :building-shadow :building-height)
                          (add-value :building-shadow (make-interval 54.9 55.1))
                          (add-value :barometer-height (make-interval 0.3 0.32))
                          (add-value :barometer-shadow (make-interval 0.36 0.37))

                          (add-value :building-height 45.0))]

    [(get-value result-system :building-height)
     (get-value result-system :building-shadow)
     (get-value result-system :barometer-height)
     (get-value result-system :barometer-shadow)
     (get-value result-system :fall-time)]))

(defn ^:export init
  []
  (.log js/console "Init")
  (.log js/console (pr-str (test))))
