(ns scudlib.helib
  (:gen-class))
(def FHE_pSize 10)
(def NTL_SP_BOUND 10)


(defn weighted_cost [cost depth] (-> 1 
                                     (bit-shift-left 16)
                                     (* depth)
                                     (+ cost)
                                     ))
(def rev reverse)
;long FindM(long k, long L, long c, long p, long d, long s, long chosen_m, bool verbose)
;dN = ceil((L+1)*FHE_pSize*cc*(k+110)/7.2); 
(defn findN [k L c p d s chosen_m verbose]
    (let [
          cc (->> c (/ 1) (+ 1.0))
          kk (+ k 110)
          ll (+ L 1)
          m 0
          i 0
          dN (->  cc
                  (* ll)
                  (* FHE_pSize)
                  (* (/ kk 7.2))
                  (Math/ceil) )
          N (if (> NTL_SP_BOUND dN) dN (throw (Throwable. "Cannot support a bound of")))
         ] )
  
  )
