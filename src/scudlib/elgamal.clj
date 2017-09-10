(ns scudlib.elgamal
(:require [clojure.math.numeric-tower :as math])
  (:gen-class))
(import java.security.SecureRandom)
(def alphabet  "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ π®ƒ©∆")

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
