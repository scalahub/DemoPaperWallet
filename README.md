# CryptoNode
[![Build Status](https://travis-ci.org/scalahub/CryptoNode.svg?branch=master)](https://travis-ci.org/scalahub/CryptoNode)

CryptoNode is a cryptocurrency library written in Scala. It can be used for implementing nodes and wallets. It supports SPV mode out of the box and can be converted to a full node by implementing the core consensus rules and the execution engine. It supports the Secp256k1 elliptic curve used in Bitcoin and its forks. 

## Currently supported features

#### Bitcoin

- Signing transactions and address generation:
  - SegWit (P2SH-P2WPKH) 
  - P2SH-P2PK
  - P2PKH
- Node connectivity:
  - Listen for blocks and transactions
  - Broadcast transaction
- SPV Mode: 
  - Merkle Blocks 
  - Bloom filters
- Local Bitcoind connectivity:
  - RPC
  - Blockchain database parser (to reuse bitcoind chain)

#### BitcoinCash 

- Signing transactions and address generation
  - P2PKH
- Node connectivity
  - Listen for blocks and transactions
  - Broadcast transaction

The following are planned in a future release:

- Bech32 addresses
- ZCash t-addresses

## Usage 
Please refer to the tests for example usage:
https://github.com/scalahub/CryptoNode/tree/master/core/src/test/scala/org/sh/cryptonode
#### Connecting to the Network

From the [TestBitcoinPeer](https://github.com/scalahub/CryptoNode/blob/master/src/test/scala/org/sh/cryptonode/btc/TestBitcoinPeer.scala "TestBitcoinPeer") example: 
```scala
val useMainNet = false // set to true for main net (default)
val node = new org.sh.cryptonode.btc.BitcoinSNode(useMainNet)
```
#### Listening for Transactions and Blocks
Once a node is created, here is how we can add handlers for listening to transactions and blocks.
```scala
node.addOnTxHandler("myTxHandler", tx => println(s"new transaction with id $tx"))
node.addOnBlkHandler("myBlkHandler", blk => println(s"new block with id $blk"))
```
#### Creating Transactions

The following code (based on the [TestTx example](https://github.com/scalahub/CryptoNode/blob/master/core/src/test/scala/org/sh/cryptonode/TestPeer.scala "TestTx example")) shows how how to create a transaction having both SegWit and non-SegWit inputs: 
```scala
val useMainNet = false // mainnet is false

// generate a big integer to use as secret 
val int = new ECCPrvKey("BB2AC60BC518C0E239D5AF9D8D051A6BDFD0D931268DCA70C59E5992", true)   // random key
// three different types of private keys (can be generated from different ints but we use the same below)
val p2wpkh_key = new PrvKey_P2SH_P2WPKH(int.bigInt, useMainNet) 
val p2sh_key = new PrvKey_P2SH_P2PK(int, useMainNet)
val p2pkh_key = new PrvKey_P2PKH(int, useMainNet)

assert(p2wpkh_key.pubKey.address == "2N6nA4btbY23GTQ4RJi3mMGTonzXN7dbphE") // (segwit)
assert(p2sh_key.pubKey.address == "2MwprvB9tUMtX4vK8zJK8K329fNu79CJgR7") // (p2sh)
assert(p2pkh_key.pubKey.address == "muLwLjVipwixXcECVMcEwgsrtfGRTB4zB1") // (p2pkh)
  
// We will create a transaction whose inputs are a mix of segwit, p2sh and p2pkh types. The inputs are below
val in0 = TxIn("0224c8875a43c482c81a508fafc10bd0f084b27b5543c228e48431985f321547", 0) // p2pkh
val in1 = TxIn("63bec90405a0c173ae928860a1e1d403e507ec225e2cdd07717c8408820d418b", 0) // segwit // 2031250 satoshis
val in2 = TxIn("db5a23f0da2502b8ef82d93453aa3be0b6e9a3ecfbfbc00818dc32b3c712d2d0", 0) // p2pkh
val in3 = TxIn("6d73e3c8f66869859dc5c1ba73f252b8db51950ebc1fc4a59dca3552a0085f9a", 0) // p2sh
val in4 = TxIn("6ce466eb0920f84cc2cfde1d359176c0baab7dc442e4e5763bf67e8fa96ee6a4", 0) // p2sh
val in5 = TxIn("b49f3d6d15f2bdd9217ba3caaf1bb1f2d9875c9657e6b0ac7a0ef841d486ad1d", 2) // p2pkh
val in6 = TxIn("b75dfb27477834b3060c8e956273bef81c62689a8b9ebb7cd4a8119508c2c798", 0) // segwit // 1015625 satoshis

// The transaction creates one output to 2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF, which is a faucet address and may 
// be either P2PKH or P2SH (without seeing a spend, it is impossible to distinguish them)

// The following output was created
val out1 = TxOut(Some("2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF"), 32206092) // total amount is slightly more than 32206092

// Below are the steps to create the fully signed transaction

// Step 1: create an unsigned transaction
val tx0 = createTxRaw(Seq(in0,in1,in2,in3,in4,in5,in6), Seq(out1)) // unsigned tx

// Step 2: sign all the p2pkh inputs
val tx1 = p2pkh_key.signTx_P2PKH (tx0, Seq(0, 2, 5)) // inputs 0, 2, 5 are P2PKH 

// Step 3: sign all the p2wpkh inputs. Note that SegWit signatures need the value in satoshis
val tx2 = p2wpkh_key.signTx(tx1, Seq((1, 2031250), (6, 1015625)))  // inputs 1, 6 are segwit. 
  
// Step 4: sign all p2sh_p2pk inputs
val signed = p2sh_key.signTx_P2SH_P2PK(tx2, Seq(3, 4)) // inputs 3, 4 are P2SH_P2PK 

// above is a byte array encoding the fully signed raw Tx
val tx = new TxParser(signed).getTx // creates a Tx object
```
#### Broadcasting transactions
Once the transaction is created, we can broadcast it using the node created earlier:  
```scala
node.pushTx(tx)
```


## Acknowledgements
Thanks to Arubi from #bitcoin-dev (Freenode) for providing several test vectors.
