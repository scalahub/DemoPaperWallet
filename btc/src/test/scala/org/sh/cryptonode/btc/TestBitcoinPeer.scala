package org.sh.cryptonode.btc

import org.sh.cryptonode.btc.BitcoinS._
import org.sh.cryptonode.net._

object TestBitcoinPeer extends App {
  isMainNet = true // set to true for main net (default)
  // Peer.debug = false // prints a lot of info
  Peer.debug = true // prints a lot of info

  val node = new BitcoinSNode(isMainNet)
  // Below shows how to add handlers for events (block or tx received)
  node.addOnTxHandler("myTxHandler", tx => println(s"[tx] $tx"))
  node.addOnBlkHandler("myBlkHandler", blk => println(s"\n[blk] $blk"))

  //node.connectTo("localhost", false) // connect to given node (false implies disable tx relay)
  node.connectToAllSeeds(true) // connect to seed nodes (false implies disable tx relay)

  Thread.sleep(10000) // wait for connnect 10 secs

  println("Started ...")

  /*  // Example below how to push tx
      val hex = "010000000138ee5b9c86d9d4fde197dbba82749bb00c8c71eb62847d18ad56491d6eb15296000000006a4730440220734765e05d315bb6b1fefdda28d959f79422d4aba1e9d8e38d61e4d0d415a0a102202e41a83c9bd71d2c817fb30b4a5229d3f7530946e01153f52e7580c26be7087d0121039f53e45f8f18b8ed294378bda342eff69b2053debf27fbede7d2d6bd84be6235ffffffff014062b0070000000017a914a9974100aeee974a20cda9a2f545704a0ab54fdc8700000000"
      val tx = new TxParser(hex.decodeHex).getTx
      BitcoinSNode.pushTx(tx) // send tx to PeerGroup to broadcast */

  println("Asking for blk ...")
  val (blk, time) = timed(node.getBlock("00000000000000000012560afe84f2bcc3df39fa42da68d1102490bbbd91af31"))
  println(s"blk received in $time millis")


  def timed[T](f: => T) = { // takes a method f outputting T and times it (i.e., finds how many millis did it take to invoke f)
    val st = System.currentTimeMillis
    (f, System.currentTimeMillis - st)
  }
}

