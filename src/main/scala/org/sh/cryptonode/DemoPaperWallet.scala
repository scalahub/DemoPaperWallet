package org.sh.cryptonode

import org.sh.cryptonode.CryptogramWrapper.{$split, $usingOs}

object DemoPaperWallet {
  def main(args: Array[String]) = {
    print(
      "Command [a](Ordinary Address) or [s](Segwit address) or [p](Private key): "
    )
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
