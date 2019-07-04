package org.sh.cryptonode

import scala.collection.JavaConverters._

object PaperWalletWrapper {

  case class OutRow(i:Int, s:String) {
    override def toString = s"$i: $s"
  }

  def $split(s:String) = s.lines.iterator().asScala.map(_.trim).filterNot(_.isEmpty).map(x => x.split(":").map(_.trim)).toArray.map(x =>
    OutRow(x(0).toInt, x(1))
  )

  def $usingOs(fn: => Unit) = {
    // https://stackoverflow.com/a/27690387/243233
    val os = new java.io.ByteArrayOutputStream
    Console.withOut(os) {fn}
    os.close()
    os.toString("UTF-8")
  }

  def $get(command:String)(seed:String, index:Int, words:Array[String]) = {
    val params = Array(seed, index.toString) ++ words
    $split($usingOs(PaperWallet.main(Array(command)++params)))
  }
  def getAddress(seed:String, index:Int, words:Array[String]) = {
    $get("-a")(seed:String, index:Int, words:Array[String])
  }
  def getSegWitAddress(seed:String, index:Int, words:Array[String]) = {
    $get("-s")(seed:String, index:Int, words:Array[String])
  }

  def getPrvKey(seed:String, index:Int, words:Array[String]) = {
    $get("-p")(seed:String, index:Int, words:Array[String])
  }
}
