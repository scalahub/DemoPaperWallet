package org.sh.cryptonode

import org.sh.cryptonode.btc.PrvKey_P2PKH
import org.sh.cryptonode.btc.PrvKey_P2SH_P2WPKH
import org.sh.cryptonode.ecc.ECCPrvKey
import org.sh.cryptonode.util.HashUtil

object TestPaperWallet {
  val addrsStr = """
      |2022: 1McvjGTP2uBntgYwGSYqqHWU2i7zXfiBjw
      |2023: 185KySbAAdGMdkQW4qg9TXRiBMJeKuo661
      |2024: 17pxVG52jUkQWzTXL16Sy81HeFmX9W3ogt
      |2025: 1P9hvyjzfeuhRi8YqjSYCGBRbEWaBdZR5B
      |2026: 18eJ57ZUYDvETGptrjbyhoo1dULBop7VvR
      |2027: 1CCQnjyvpQbBrScfJV5EZyX9rs3o31Fvbs
      |2028: 14Pbww48aTbSnmx5sx71juHwJohrajNRh8
      |2029: 18LfwbbiypEbSMmso4VWbbpmRGPe8RSpbM
      |2030: 1LjyajcExYecXePXYqqWWion7WqWVDR37j
      |2031: 1LRem3VS8ThAy4hP8QkSYfeW5zqDJmvkjU
    """.stripMargin
  val segAddrsStr =
    """
      |2022: 33uqHrHJRkSXeYNJPoQdZ4Vxe3vS9zpjaS
      |2023: 3Kn3rRFAJmF84jkoQWjxBC6H7FrQkkHrbP
      |2024: 39kLVQS5E6jcTTXtLCCzWEKUGpRRs3aZBh
      |2025: 3DVUZqMVJSYZjFTY5LGiG77dpf5XwCER67
      |2026: 3PXz4wUhLTZsZMk3pnxFM9PEX4hwTrn5cc
      |2027: 3K5PkFxAZ2PXesFpG4dt6tnkqLmkD1hTLc
      |2028: 3HAdjW2ti1oB3hc5tr3hBmSjM6iJvGeBW1
      |2029: 36dPA4rqHe4ai6h7XVXVbWyfbzAVkvfumC
      |2030: 38bUuVTthVNYPVMnFpuzhofhwLw8sYC4kb
      |2031: 3HXMUWxzbRynwHtRVJPQxMLtGBtEzfqVcq
    """.stripMargin
  val keysStr =
    """
      |2022: L3yU5wUrsYa6nM6FnRdEQKWDGzt54DuejUi66xpg3U6y8XAieDxP
      |2023: KxKSYUSsxConUth7pWMx7b8Te5zfJeQaUBKaWyMNVWhXKUoptUWh
      |2024: KzL1h4D1wYf5k3CFAGjKjC8npPaBjgyijta76SVWmrMon9pXYGZL
      |2025: KxWv5cz89HrWUeo9DzjV5AZDgJ2nvRipVaXjjuyRSjh3irVWLUW4
      |2026: L1diHrDBEQvFTBarxymcUoFioLmvqpJck3fXJzaaraTkQXuqnWbz
      |2027: L5Aj4BkZGihQubKwJLtFFnGx7xWgK7oTeH7MzbrNGQUXr9EqtiyP
      |2028: KyvSYjoCbzc95sLWc3RgDrJMznmr4uDckFchaEPHeofUQDLBEdat
      |2029: KwxYEYigKdB8mvBrUe82rVF2PhMvY8oAuFY6k5HccriBy3BRnGF4
      |2030: L5XiAd9KSff1MfTPVeDmtn7qp6uMRjNtCwTbZezDfKtaecPxYJW6
      |2031: Kz1L7QLG1fcb89DFUtW2nj9SWzoZcNbYL3LyStRb6cTbvV8DUAcT
    """.stripMargin


  case class OutRow(i:Int, s:String) {
    override def toString = s"$i: $s"
  }
  def split(s:String) = s.lines.map(_.trim).filterNot(_.isEmpty).map(x => x.split(":").map(_.trim)).toArray.map(x =>
    OutRow(x(0).toInt, x(1))
  )
  def identical(left:Array[OutRow], right:Array[OutRow]) =
    left.size == right.size && {(left zip right).forall {
        case (l, r) => l.i == r.i && l.s == r.s
      }
    }
  val (addrs, segAddrs, keys):(Array[OutRow], Array[OutRow], Array[OutRow]) = (split(addrsStr), split(segAddrsStr), split(keysStr))

  def main(args:Array[String]):Unit = {
    val seed = "topSecretSeed"
    val index = "2022"
    val words = Array("correct", "horse", "battery", "staple")
    val params = Array(seed, index) ++ words

    // -p topSecretSeed 2022 correct horse battery staple

    // test 1
    // https://stackoverflow.com/a/27690387/243233
    val os1 = new java.io.ByteArrayOutputStream
    Console.withOut(os1) {PaperWallet.main(Array("-a")++params)}
    os1.close()
    val out1 = split(os1.toString("UTF-8"))
    assert(identical(out1, addrs), "address mismatch")

    // test 2
    val os2 = new java.io.ByteArrayOutputStream
    Console.withOut(os2) {PaperWallet.main(Array("-s")++params)}
    os2.close()
    val out2 = split(os2.toString("UTF-8"))
    assert(identical(out2, segAddrs), "segwit-address mismatch")

    // test 3
    val os3 = new java.io.ByteArrayOutputStream
    Console.withOut(os3) {PaperWallet.main(Array("-p")++params)}
    os3.close()
    val out3 = split(os3.toString("UTF-8"))
    assert(identical(out3, keys), "key mismatch")
    println("All wallet tests passed")
  }
}

