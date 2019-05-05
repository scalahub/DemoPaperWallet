
package org.sh.cryptonode.net

import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicLong
import org.sh.cryptonode.btc.BitcoinS._
import org.sh.cryptonode.util.StringUtil._
import org.sh.cryptonode.util.BytesUtil._
import org.sh.cryptonode.btc.DataStructures.Tx
import org.sh.cryptonode.ecc.Util._
import org.sh.cryptonode.util.HashUtil._
import org.sh.cryptonode.btc.BitcoinUtil._
import NetUtil._
import Payloads._

object DataStructures {

  val ipv4to6prefix = "00000000000000000000FFFF".decodeHex
  val nonce = new AtomicLong(0)

  class Payload(val bytes:Seq[Byte]) {
    val checkSum = dsha256(bytes).take(4)
    val checkSumHex = checkSum.encodeHex
    val len:UInt32 = bytes.size     
    override def toString = bytes.toArray.encodeHex    
  }
  
  val headerLen = 24 // headers are always 24 bytes. Setting it once here to avoid hardcoding elsewhere
  
  /* Message Header: https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
     F9 BE B4 D9                                                                   - Main network magic bytes
     76 65 72 73 69 6F 6E 00 00 00 00 00                                           - "version" command
     64 00 00 00                                                                   - Payload is 100 bytes long
     3B 64 8D 5A                                                                   - payload checksum      
     e3:e1:f3:e8   bch */
    
  
  case class P2PHeader(command:String, checkSum:Array[Byte], payloadLen:Int) {
    val checkSumHex:String = checkSum.encodeHex
    val bytes:Array[Byte] = getFixedStringBytes(command, 12).toArray ++ UInt32(payloadLen).bytes ++ checkSum
  }
  
  class P2PMsg(command:String, payload:Payload){
    def this(command:String) = this(command, new Payload(Nil))
    val header = P2PHeader(command, payload.checkSum, payload.len) 
    val bytes:Array[Byte] = header.bytes ++ payload.bytes
    override def toString = s"command: $command, payload: $payload"
  }
  
  type InvType = UInt32
  
  object ERROR extends InvType(0) // not used as of now
  object MSG_TX extends InvType(1)
  object MSG_BLOCK extends InvType(2)
  object MSG_FILTERED_BLOCK extends InvType(3) // not used as of now 
  object MSG_CMPCT_BLOCK extends InvType(4) // not used as of now
  
  case class InvVector(invType:InvType, hash:Char32) { 
    /* IMPORTANT: hash is in internal byte order (i.e. BIG ENDIAN, not little ENDIAN as displayed in block explorer) 
       So for example, if the block hash as displayed in explorer is [000000...wxyz] in hex, here it will be sent as [yzwx...0000]
       The same holds for txids */      
    def this(invType:InvType, hashRpcHex:String) = this(invType, hashRpcHex:Char32)
    val bytes = invType.bytes ++ hash.bytes 
    override def toString = s"InvType: $invType, hash: ${hash.rpcHash}"
  }
  
  val verAckCmd = "verack"
  val getAddrCmd = "getaddr"
  val pingCmd = "ping"
  val invCmd = "inv"
  val pongCmd = "pong"
  val addrCmd = "addr"
  val versionCmd = "version"
  val rejectCmd = "reject"
  val alertCmd = "alert" // not processed as of now
  val getDataCmd = "getdata" 
  val notFoundCmd = "notfound" 
  val txCmd = "tx" 
  val blockCmd = "block" 
  val filterLoad = "filterload" 
  val memPool = "mempool" 
  val filterAdd = "filteradd" 
  val filterClear = "filterclear" 
  
  // below used for sending to others
  object VerAckMsg extends P2PMsg(verAckCmd)
  
  object GetAddrMsg extends P2PMsg(getAddrCmd) // unused as of now
  
  case class AddrMsg(inetAddresses:Array[InetSocketAddress]) extends P2PMsg(addrCmd, new AddrPayload(inetAddresses)) // unused as of now
  
  case class GetDataMsg(invVectors:Seq[InvVector]) extends P2PMsg(getDataCmd, new InvPayload(invVectors)) {
    def this(blockHash:String) = this(Seq(InvVector(MSG_BLOCK, blockHash)))
  }
  
  case class FilterLoadMsg(filter:BloomFilter) extends P2PMsg(filterLoad, FilterLoadPayload(filter))
  
  object MemPoolMsg extends P2PMsg(memPool)

  case class FilterAddMsg(data:Array[Byte]) extends P2PMsg(filterAdd, FilterAddPayload(data))

  object FilterClearMsg extends P2PMsg(filterClear)
  
  case class PushTxInvMsg(txRpcHash:String) extends P2PMsg(invCmd, new InvPayload(InvVector(MSG_TX, txRpcHash))) 
  
  case class VersionMsg(version:Int,  userAgent:String, serviceBit:Int, local:InetSocketAddress, remote:InetSocketAddress, relay:Boolean) 
    extends P2PMsg(versionCmd, new VersionPayload(version, serviceBit,  userAgent, local, remote, relay))
  
  case class PongMsg(ping:PingPayload) extends P2PMsg(pongCmd, PongPayload(ping))
  
  case class TxMsg(tx:Tx) extends P2PMsg(txCmd, new Payload(tx.serialize)) 
}
