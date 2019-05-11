package org.sh.cryptonode.bch

import org.sh.cryptonode.btc.BitcoinS._
import org.sh.cryptonode.net._

object TestUAHFPeer extends App {
  isMainNet = true
  Peer.debug = true
  val node = new BitcoinCashSNode(isMainNet)
  node.connectToAllSeeds(true)  
  node.addOnTxHandler("myTxHandler", tx => println(s"[tx] $tx"))
  node.addOnBlkHandler("myBlkHandler", blk => println(s"[blk] $blk"))

  //node.connectTo("localhost", true) // for local or any specific node
}
object TestABCPeer {
  def main(a:Array[String]):Unit = if (a.size == 2) {    
    isMainNet = a(1).toBoolean
    Peer.debug = true
    val node = new BitcoinCashSNode(isMainNet)
    node.connectTo(a(0), true)
    node.addOnTxHandler("myTxHandler", tx => println(s"[tx] $tx"))
    node.addOnBlkHandler("myBlkHandler", blk => println(s"[blk] $blk"))

  } else println("Usage java -cp test.jar org.sh.cryptonode.TestABCPeer <host> <isMainNet> ")
}







