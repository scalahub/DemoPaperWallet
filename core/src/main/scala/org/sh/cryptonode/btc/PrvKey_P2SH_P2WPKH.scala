package org.sh.cryptonode.btc

import org.sh.cryptonode.btc.BitcoinUtil._
import org.sh.cryptonode.btc.DataStructures._
import org.sh.cryptonode.ecc._

class PrvKey_P2SH_P2WPKH (bigInt:BigInt, mainNet:Boolean) extends PrvKey(new ECCPrvKey(bigInt, true), mainNet) {
   
  lazy val pubKey = new PubKey_P2SH_P2WPKH(eccPrvKey.eccPubKey.point, mainNet)
  // SEGWIT 3Address
  
  def signTx(rawTx:Array[Byte], whichInputsAmts:Seq[(Int, BigInt)]) = { // which input indices to sign, with amount
    import eccPrvKey._
    import pubKey._
    val tx = new TxParser(rawTx).getTx
    whichInputsAmts.map{
      case (i, value) => 
        val hash = tx.getHashSigned_P2SH_P2WPKH(i, value, doubleHashedPubKeyBytes)
        val sig = signHash(hash) ++ Array(0x01.toByte) // append a 0x01 to indicate SIGHASH_ALL
        tx.wits(i) = TxWit(Seq(sig, pubKey.bytes))
        // finally set the scriptSig for input script (scriptSig is always a push of redeemScript)
        tx.ins(i).setScriptSig(redeemScript.size.toByte +: redeemScript) // set scriptSig 
    }
    createSegWitTx(tx.version, tx.ins zip tx.wits, tx.outs, tx.lockTime)
  }
}
