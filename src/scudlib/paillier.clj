(ns scudlib.paillier
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
        {n :n np1 :np1 n2 :n2} pubkey 
        one (biginteger 1)
        x (-> np1
              (.modPow l n2)
              (.subtract one)
              (.divide n)
              (.modInverse n))
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
            one (biginteger 1)
            lambda (lcm (.subtract p one) (.subtract q one))
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
        {n :n bits :bits n2 :n2} publickey
        r (loop []
            (let [
                  r (new BigInteger bits 1 rng)]
              (if (>= (.compareTo r n) 0)
                (recur)
                r
                )
              )
            )
        ] (.modPow r n n2)
  )
)

(defn randomize [publickey a]
  (let [
        rn (getRN publickey)
        n2 (:n2 publickey)
        
        ]
    (.mod (.multiply a rn) n2)
    )
  )

(defn encrypt [publickey mm]
  (let
      [
       m (biginteger mm)
       one (biginteger 1)
       {n :n n2 :n2} publickey
       inner (-> n
                 (.multiply m)
                 (.add one)
                 (.mod n2)) 
       ]
    (randomize publickey inner)
    ))

(defn decrypt [privatekey c]
  (let [
        {lambda :lambda x :x {n :n n2 :n2} :pubkey} privatekey
        one (biginteger 1)
       ]
      (-> c
          (.modPow lambda n2)
          (.subtract one)
          (.divide n)
          (.multiply x)
          (.mod n)
          )
      ))

