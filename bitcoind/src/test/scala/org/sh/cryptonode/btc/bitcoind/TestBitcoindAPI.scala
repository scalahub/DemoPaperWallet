
package org.sh.cryptonode.btc.bitcoind


import org.sh.cryptonode.btc.DataStructures.{TxIn, TxOut}
import org.sh.cryptonode.btc.PrvKey_P2PKH
import org.sh.cryptonode.ecc.ECCPrvKey

object TestBitcoindAPI extends App {
  val mainNet = true
  val compressed = true
  val b = new BitcoindAPI("user", "password", "http://localhost:8332")
  
  1 to 10000 foreach {i =>
    val eccPrvKey = new ECCPrvKey(i * 1000, compressed)
    val a = new PrvKey_P2PKH(eccPrvKey, mainNet).pubKey.address
    b.importAddress(a) // rescan is always false
  }
  println("tx "+b.getTransaction("b46d059b6984485e32a9743d8da7125c01f75ce5b39366eb402599d41cd82291"))
  println("confs "+b.getConfirmations("b46d059b6984485e32a9743d8da7125c01f75ce5b39366eb402599d41cd82291"))
  println("block "+b.getBlock("6409a5bb8f5dd74495fc16b2c5aeab40b2cdc0d18a716595479bb6ff2b60bf6b"))
  println("version "+b.getSoftwareVersion)
  println("import address1 "+b.importAddress("2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF"))
  println("import address2 "+b.importAddress("n4obYN9q8h4GCazSfD9j7gUUSWwDqrnkWm"))
  println("getAddresses")
  b.getBlock(b.getBestBlockHash).txs foreach println
  val hex = b.createRawTransaction(
    Array(
      TxIn("2550fd204745361fd828042423bcde2be000926bd2d4db901749b85f1592e24f", 0), 
      TxIn("b2b7217210316e680ecd5ebbe38b6debfc1e2486f6f9a1cca6bdfe34ff32b274", 1),
      TxIn("4792d9c13c37dc08dc05953ebf5533480927b961bcca59421a8f863aedb1e719", 2)
    ), 
    Array(
      TxOut(Some("2N8hwP1WmJrFF5QWABn38y63uYLhnJYJYTF"), 100000),
      TxOut(Some("tmGL4Hjcc8J1rkikD7jQqRvQvrpKtq7GbJW"), 200000)
    )
  )
  println("hex "+hex)
}

