
package org.sh.cryptonode

import org.sh.cryptonode.util.Base64

object TestBase64 extends App {
  val s = "Hello"
  val a = Base64.encodeBytes(s.getBytes("UTF-16"))
  val x = new String(Base64.decode(a), "UTF-16")
  require(x == s, s"Reqired $x (found $s")
  1 to 1000 foreach{i => 
    val n = scala.util.Random.nextInt(1000).abs
    val in = scala.util.Random.nextString(n)
    val out = Base64.encodeBytes(in.getBytes("UTF-16"))
    require(in == new String(Base64.decode(out), "UTF-16"), s"Reqired $in (found $out)")
  }
  println("All Base64 test passed")
}