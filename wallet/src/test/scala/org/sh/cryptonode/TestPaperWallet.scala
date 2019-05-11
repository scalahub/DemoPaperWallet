package org.sh.cryptonode

import org.sh.cryptonode.btc.PrvKey_P2PKH
import org.sh.cryptonode.btc.PrvKey_P2SH_P2WPKH
import org.sh.cryptonode.ecc.ECCPrvKey
import org.sh.cryptonode.util.HashUtil

object TestPaperWallet {
  /* INSTRUCTIONS TO RUN: 
   *   First create a fat jar using sbt
   *     sbt_prompt> test:assemble
   *   This will create a jar target/scala-2.12/CryptoNode-assembly-0.1.jar 
   *   Then run the jar as follows:
   *     java -cp target/scala-2.12/CryptoNode-assembly-0.1.jar org.sh.cryptonode.PaperWallet -a topSecretSeed 2022 correct horse battery staple
   *  
   *   (In the above command, the seed is topSecretSeed, the start index is 2022 and the words are correct horse battery staple)
   */
  /* EXPERIMENTAL WALLET: Do not use for storing real funds. Use only for testnet coins.
   * Takes as input:
   * 1. Option to output ordinary address (-a), segwit address (-s) or private key (-p)
   * 3. Seed
   * 3. Start index
   * 4. sequence of words
   *
   * WARNING: DO NOT USE SEGWIT ADDRESSES FOR STORING BCH
   *
   * Outputs 10 addresses of private keys generated from the words starting from index
   * Example:
   *    java -cp <jar_path> -a topSecretSeed 2022 correct horse battery staple
   *
   *    gives addresses from 2022 to 2031 using the words correct horse battery staple and seed topSecretSeed
   *
   * In order to recover the keys, the seed, the index and the words are needed
   *
   */
  def hash(bytes:Array[Byte]) = HashUtil.sha256Bytes2Bytes(bytes)
  def main(args:Array[String]):Unit = {
    if (args.size < 4) {
      println(
        """
Usage java -cp <jar> org.sh.cryptonode.PaperWallet <option> <seed> <start index> <word1> <word2> ...
<option> can be one of -a, -s or -p denoting ordinary address, segwit address or private key respectively.
<seed> is any secret string used to initialized the wallet. Keep it long
<start index> is a BigInteger denoting the starting index for generating the next 10 addresses
<word1>, <word2> ... is an unlimited sequence of words. Use at least 10 for strong security.

Example: java -cp <jarPath> org.sh.cryptonode.PaperWallet -a topSecretSeed 2022 correct horse battery staple
This will output will output addresses from 2022 to 2031 using the words: correct horse battery staple and the seed: topSecretSeed
""")
    } else {
      val opt = args(0).toLowerCase
      val isCompressed = true
      val isMainNet = true

      val fn = opt match {
        case "-s" => i:BigInt => new PrvKey_P2SH_P2WPKH(i, isCompressed).pubKey.address
        case "-a" => i:BigInt => new PrvKey_P2PKH(new ECCPrvKey(i, isCompressed), isMainNet).pubKey.address
        case "-p" => i:BigInt =>
          //new PrvKey_P2SH_P2WPKH(i, isCompressed).getWIF // should output same as below
          new PrvKey_P2PKH(new ECCPrvKey(i, isCompressed), isMainNet).getWIF
        case any => throw new Exception("Unknown option. Must be -a, -s or -p")
      }

      val seed = args(1)
      if (seed.size < 10) throw new Exception("Seed must be at least 10 chars")
      val index = BigInt(args(2))
      val words = args.drop(3).map(_.getBytes("UTF-16"))

      val numWords = words.size
      if (numWords < 2) throw new Exception("At least two words are required")
      var tmp = hash(words(1) ++ seed.getBytes("UTF-16"))
      val numIter = words.size * 193

      1 to numIter foreach{i =>
        tmp = hash(words(i % numWords) ++ hash(tmp))
      }
      words.reverse.foreach{w =>
        tmp = hash(hash(w) ++ tmp)
      }
      0 to 9 foreach{i =>
        val int = i + index
        println(int + ": " +fn(BigInt(hash(tmp ++ int.toByteArray)).mod(org.sh.cryptonode.ecc.Util.n)))
      }
    }
  }
}

