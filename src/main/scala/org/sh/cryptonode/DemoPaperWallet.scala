package org.sh.cryptonode

import org.sh.cryptonode.CryptogramWrapper.{$split, $usingOs}

import scala.collection.JavaConverters._

object DemoPaperWallet {
  def main(args: Array[String]) = {
    print("Command [a]ddress or [s]egwit address or [p]private key: ")
    val command = scala.io.StdIn.readLine
    print("Index (int): ")
    val index = BigInt(scala.io.StdIn.readLine)
    val seed = "ExWs4Hk8ZlG6DsG28A4ksW3T54s6eIYoWrRtkaRwEaYV5NKlRA"
    print("Enter words: ")
    val words = scala.io.StdIn.readLine.split(" ")
    $get("-" + command)(seed, index, words) foreach println
  }

  def $get(
      command: String
  )(seed: String, index: BigInt, words: Array[String]) = {
    val params = Array(seed, index.toString(10)) ++ words
    $split($usingOs(Cryptogram.main(Array(command) ++ params)))
  }

}
