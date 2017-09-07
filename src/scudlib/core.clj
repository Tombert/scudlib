(ns scudlib.core
  (:gen-class))
(import java.security.SecureRandom)
(defn lcm [a b] (.divide (.multiply a b) (.gcd a b)))
(defn makePublicKey [bits nn]
  (let [n (biginteger nn)]
  {
    :bits (biginteger bits)
    :n n
    :n2 (.pow n 2)
    :np1 (.add n (biginteger 1))
    :rncache []
  })
  )

(defn makePrivateKey [lambda pubkey]
  (let [
        l (biginteger lambda)
        n (:n pubkey)
        np1 (:np1 pubkey)
        n2 (:n2 pubkey)
        one (biginteger 1)
        x (.modInverse (.divide (.subtract (.modPow np1 l n2) one) n) n )
       ]
  {
      :lambda l
      :pubkey pubkey
      :x x
  }))

(defn getprobablyprime [rng modbits]
  (loop [p (biginteger 10)]
    (if (.isProbablePrime p 10)
      p
      (recur (new BigInteger (bit-shift-right modbits 1) 1 rng))
    )
  )
)

(defn generateKeys [modulusbits]
  (loop [
         rng (new SecureRandom )
        ]
  (let [
        p (getprobablyprime rng modulusbits)
        q (getprobablyprime rng modulusbits)
        n (.multiply p q)
        ]
    (if (and (not (.testBit n (- modulusbits 1))) (== (.compareTo p q) 0))
      (recur rng)
      (let [
            pub (makePublicKey modulusbits n)
            lambda (lcm (.subtract p (biginteger 1)) (.subtract q (biginteger 1)))
            sec (makePrivateKey lambda pub)
            ]
        {:pub pub :sec sec}
        )
      )
    )
  )
)

(defn add [publickey a b]
  (.remainder (.multiply a b) (:n2 publickey))
  )

(defn mult [publickey a b]
  (.modPow a b (:n2 publickey))
  )

(defn getRN [publickey]
  (let [
        rng (new SecureRandom)
        r (loop []
            (let [
                  bits (:bits publickey)
                  n (:n publickey)
                  r (new BigInteger bits 1 rng)]
              (if (>= (.compareTo r n) 0)
                (recur)
                r
                )
              )
            )
        ] (.modPow r (:n publickey) (:n2 publickey))
  )
)

(defn randomize [publickey a]
  (let [
        rn (getRN publickey)]
    (.mod (.multiply a rn) (:n2 publickey))
    )
  )

(defn encrypt [publickey mm]
  (let
      [
       m (biginteger mm)
       one (biginteger 1)
       inner (.mod (.add (.multiply (:n publickey ) m ) one) (:n2 publickey) )]
    (randomize publickey inner)
    ))

(defn decrypt [privatekey c]
  (.mod (.multiply (.divide (.subtract (.modPow c (:lambda privatekey) (:n2 (:pubkey privatekey) )) (biginteger 1)) (:n (:pubkey privatekey))) (:x privatekey)) (:n (:pubkey privatekey))))
