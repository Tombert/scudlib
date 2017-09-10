(ns scudlib.elgamal
(:require [clojure.math.numeric-tower :as math])
(:require [clojure.string :as str])
  (:gen-class))
(import java.security.SecureRandom)
(def alphabet  "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ π®ƒ©∆")
(defn to10 [x] (let [
                     initstate {:y 0 :p 1}
                     xx  (str/split alphabet #"") 
                     n (count xx)
                     nrange (range 0 n)
                    ] 
                 ((reduce (fn [state i] 
                          (let [
                                {p :p y :y} state
                                cc (alphabet i)
                                newp  (* p (count alphabet))
                                newy (+ y (* (.indexOf alphabet cc) p))
                                ] 
                           {:p newp :y newy}
                            ) 
                           ) initstate nrange ) :y )))
(defn toAlpha [acc x] 
                   (let [
                         n 4
                         myrange (range 1 n)
                         ] 
                     (map (fn [i] (
                                   (let [
                                         p (math/expt (count alphabet) i)
                                         l (-> x 
                                               (/ p) 
                                               math/floor 
                                               int)
                                         ])
                                   )) myrange)
                     )
                    )
