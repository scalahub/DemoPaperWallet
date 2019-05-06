# CryptoNode

CryptoNode is a cryptocurrency library written in Scala. It can be used for implementing nodes and wallets for Bitcoin and its related forks. It supports SPV mode out of the box and can be converted to a full node by implementing the core consensus rules and the execution engine. It supports the Secp256k1 elliptic curve used in Bitcoin and its forks. 

## Currently supported features

### Bitcoin

- Signing transactions and address generation:
  - SegWit (P2SH-P2WPKH) 
  - P2SH-P2PK
  - P2PKH
- Node connectivity:
  - Listen for blocks and transactions
  - Broadcast transaction
  - All network commands in Bitcoin
- SPV Mode: 
  - Merkle Blocks 
  - Bloom filters
- Local Bitcoind connectivity:
  - RPC
  - Blockchain database parser (to reuse bitcoind chain)

### BitcoinCash 

- Signing transactions and address generation
  - P2PKH
- Node connectivity
  - Listen for blocks and transactions
  - Broadcast transaction

## Future additions planned

- Bitcoin consensus rules
- ZCash with t-address
- Bitcoin Bech32 address 
- BitcoinCash CashAddr (bech32)
- Consensus rules and chain validation

## Usage 
Please refer to the tests for example usage:
https://github.com/scalahub/CryptoNode/tree/master/src/test/scala/org/sh/cryptonode

Below is an example of how to create SegWit transactions:
(borrowed from https://github.com/scalahub/CryptoNode/tree/master/src/test/scala/org/sh/cryptonode/TestTx.scala)

```scala
val key = new ECCPrvKey("BB2AC60BC518C0E239D5AF9D8D051A6BDFD0D931268DCA70C59E5992", true)   // random key
val p2wpkh_key = new PrvKey_P2SH_P2WPKH(key.bigInt, false) // mainnet is false
val p2sh_key = new PrvKey_P2SH_P2PK(key, false) // mainnet is false
val p2pkh_key = new PrvKey_P2PKH(key, false) // mainnet is false
assert(p2wpkh_key.pubKey.address == "2N6nA4btbY23GTQ4RJi3mMGTonzXN7dbphE") // (segwit)
assert(p2sh_key.pubKey.address == "2MwprvB9tUMtX4vK8zJK8K329fNu79CJgR7") // (p2sh)
assert(p2pkh_key.pubKey.address == "muLwLjVipwixXcECVMcEwgsrtfGRTB4zB1") // (p2pkh)
  
// Send some coins to the above addresses. During testing, the following coins were used:
val in0 = TxIn("0224c8875a43c482c81a508fafc10bd0f084b27b5543c228e48431985f321547", 0) // p2pkh
val in1 = TxIn("63bec90405a0c173ae928860a1e1d403e507ec225e2cdd07717c8408820d418b", 0) // segwit // 2031250 satoshis
val in2 = TxIn("db5a23f0da2502b8ef82d93453aa3be0b6e9a3ecfbfbc00818dc32b3c712d2d0", 0) // p2pkh
val in3 = TxIn("6d73e3c8f66869859dc5c1ba73f252b8db51950ebc1fc4a59dca3552a0085f9a", 0) // p2sh
val in4 = TxIn("6ce466eb0920f84cc2cfde1d359176c0baab7dc442e4e5763bf67e8fa96ee6a4", 0) // p2sh
val in5 = TxIn("b49f3d6d15f2bdd9217ba3caaf1bb1f2d9875c9657e6b0ac7a0ef841d486ad1d", 2) // p2pkh
val in6 = TxIn("b75dfb27477834b3060c8e956273bef81c62689a8b9ebb7cd4a8119508c2c798", 0) // segwit // 1015625 satoshis

// the following output was created
// Note 2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF faucet address
val out1 = TxOut(Some("2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF"), 32206092) // total amount is slightly more than 32206092
  
val tx0 = createTxRaw(Seq(in0,in1,in2,in3,in4,in5,in6), Seq(out1)) // unsigned tx
val tx1 = p2pkh_key.signTx_P2PKH (tx0, Seq(0, 2, 5)) // inputs 0, 2, 5 are P2PKH 
val tx2 = p2wpkh_key.signTx(tx1, Seq((1, 2031250), (6, 1015625)))  // inputs 1, 6 are segwit. 
// SegWit signatures need the input value in satoshis as well
  
val signed = p2sh_key.signTx_P2SH_P2PK(tx2, Seq(3, 4)) // inputs 3, 4 are P2SH_P2PK 
// above is a byte array encoding the raw Tx
val parsed = new TxParser(signed).getTx // creates a Tx object
```
## Acknowledgements
Thanks to Arubi from #bitcoin-dev (Freenode) for providing several test vectors.
