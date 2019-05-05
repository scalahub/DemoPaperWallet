
package org.sh.cryptonode.net

import java.net.InetSocketAddress
import org.sh.cryptonode.btc.BitcoinS._
import org.sh.cryptonode.util.StringUtil._
import org.sh.cryptonode.net.DataStructures._
import org.sh.cryptonode.util.BytesUtil._
import org.sh.cryptonode.ecc.Util._
import org.sh.cryptonode.util.HashUtil._
import org.sh.cryptonode.btc.BitcoinUtil._
import NetUtil._

object Payloads {
  
  case class PingPayload(nonce:UInt64) extends Payload(nonce.bytes)
  case class PongPayload(ping:PingPayload) extends Payload(ping.nonce.bytes)
  
  case class FilterLoadPayload(f:BloomFilter) extends Payload(
    getCompactIntBytes(f.bytes.size)++f.bytes++f.nHashFuncs.bytes++f.nTweak.bytes++f.nFlags.bytes
  ) 
  
  case class FilterAddPayload(data:Array[Byte]) extends Payload(data) {
    if (data.size > 520) throw new Exception(s"Data too large (${data.size} bytes). Max 520 bytes permitted")
  }
  
  case class InvPayload(invVectors:Seq[InvVector]) extends {   
    val numItems:VarInt = invVectors.size
  } with Payload(numItems.bytes ++ invVectors.flatMap(_.bytes)) {
    def this(invVector:InvVector) = this(Seq(invVector))
    override def toString = "invPayload: "+numItems.bigInt+": "+(if (numItems.bigInt > 0) invVectors(0).toString else "None")
  }
  
  case class AddrPayload(netAddrs:Seq[NetAddrPayload]) extends {
    val numItems:VarInt = netAddrs.size
  } with Payload(numItems.bytes ++ netAddrs.flatMap(_.bytes)) {
    def this(netAddr:NetAddrPayload) = this(Seq(netAddr))
    def this(address:Array[Byte], port:Int) = this(new NetAddrPayload(address, port))
    def this(sAddresses:Array[InetSocketAddress]) = this(
      sAddresses.map(sAddress =>       
        new NetAddrPayload(sAddress.getAddress.getAddress, sAddress.getPort)
      )
    )
    def this(sAddress:InetSocketAddress) = this(Array(sAddress))
  }
  
  case class VersionPayload(
    version:Int32, services:UInt64, timeStamp:Int64, addr_recv:NetAddrPayload, addr_from:NetAddrPayload,
    nonce:UInt64, userAgent:String, startHeight:Int32, relay:Boolean
  ) extends Payload(
    version.bytes ++ services.bytes ++ timeStamp.bytes ++ addr_recv.bytes ++ addr_from.bytes ++ 
    nonce.bytes ++ getVarStringBytes(userAgent) ++ startHeight.bytes ++ relay.bytes
  ) {
    def this(
      version:Int, serviceBit:Int, userAgent:String, 
      local:InetSocketAddress, remote:InetSocketAddress, relay:Boolean
    ) = this(
      version, BigInt(serviceBit), getTimeSec, 
      new NetAddrPayload(local.getAddress.getAddress, local.getPort, true),
      new NetAddrPayload(remote.getAddress.getAddress, remote.getPort, true),
      BigInt(nonce.getAndIncrement), userAgent, 1, relay
    )
  }
  /*    https://en.bitcoin.it/wiki/Protocol_documentation#Network_address
   (12 bytes 00 00 00 00 00 00 00 00 00 00 FF FF, followed by the 4 bytes of the IPv4 address). 
   time is 4 bytes (uint32) for version >= 31402, not present in version message */
  case class NetAddrPayload(time:UInt32, services:UInt64, address:Array[Byte], port:UInt16)(isVersionMsg:Boolean) extends {
    val isIPv6:Boolean = address.size == 16
    val addressBytes:Seq[Byte] = if (isIPv6) address else ipv4to6prefix ++ address 
    val timeBytes:Seq[Byte] = (if (isVersionMsg) Nil else time.bytes)
  } with Payload(timeBytes ++ services.uint64.bytes ++ addressBytes ++ port.bytes) {
    def this(address:Array[Byte], port:Int, isVersionMsg:Boolean) = this(getTimeSec, BigInt(0), address, port)(isVersionMsg)
    def this(address:Array[Byte], port:Int) = this(getTimeSec, BigInt(0), address, port)(false)
    if (address.size != 4 && address.size != 16) throw new Exception("Invalid bytes for address: "+address.encodeHex)
  }
}
