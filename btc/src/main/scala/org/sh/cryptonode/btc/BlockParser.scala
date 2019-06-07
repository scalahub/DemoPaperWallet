package org.sh.cryptonode.btc

import org.sh.cryptonode.btc.DataStructures._
import org.sh.cryptonode.util.HashUtil._

class BlockHeaderParser(bytes:Array[Byte]) extends TxParser (bytes) {
  // Header is the first 80 bytes, which has following data:
  val version = getNext4SInt // signed
  val prevBlockHash = getNext32Hash
  val merkleRoot = getNext32Hash  
  val time = getNext4UInt // unsigned
  val nBits = getNextBytes(4)
  val nonce = getNext4UInt // unsigned
  val hash = getHashed(getBytes(0, 79)) 
  
  val header = BlkHeader(hash, prevBlockHash, time, version, merkleRoot, nBits, nonce)
  
  // if header not needed, replace above by: incrCtr(80) // skip 80 bytes of header
} 

class BlockParser(bytes:Array[Byte]) extends BlockHeaderParser (bytes) { 
  lazy val txs:Seq[Tx] = 1 to getCompactInt map (_ => getTx) // first getCompactInt returns numTx
  
  // https://bitcoin.org/en/developer-reference#raw-transaction-format        
  def getBlock = Blk(header, txs)
}
