# scudlib

Scudlib is an implementation of the Paillier algorithm for basic homomorphic encryption. 

On the roadmap is to add other algorithms, and to modularize the functions for generating keys

## Installation

This is a pretty straightforward install.  Making sure [Leiningen](https://leiningen.org/) is installed, simply clone the repo, and type `lein repl` and then `(use scudlib.paillier)`.

## Usage

To do basic encryption, you first need to generate keys: 

```
(def mykeys (generateKeys 100))
```
Where 100 is the number of bits. This should generate a hash like this: 

```
{:pub {:bits 100, :n 595667010391440934419099824371, :n2 354819187268677002147288563688095410977685067815803045545641, :np1 595667010391440934419099824372, :rncache []}, :sec {:lambda 19855567013047979005325077740, :pubkey {:bits 100, :n 595667010391440934419099824371, :n2 354819187268677002147288563688095410977685067815803045545641, :np1 595667010391440934419099824372, :rncache []}, :x 563557312431137071852827229601}}
```

I find it useful to separate the public and private keys: 

```
(def pubkey (:pub mykeys))

(def privkey (:sec mykeys))
```

Once we have our keys, we can encrypt some numbers. 

```
(def encnum (encrypt pubkey 10))
; encnum is 37471

(def encnum2 (encrypt pubkey 20))
; encnum2 is 10771

```

From here, we can add the values together. 

```
(def summednum (add pubkey encnum encnum2))
; summednum is 463524
```

Now, to make sure our addition was successful, we can decrypt the summed number.

```
(decrypt privkey summednum)
30
```

