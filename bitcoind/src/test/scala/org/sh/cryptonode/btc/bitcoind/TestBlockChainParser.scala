
package org.sh.cryptonode.btc.bitcoind

import org.sh.cryptonode.btc.bitcoind.blockchain.BlockChainParser


object TestBlockChainParser extends BlockChainParser {
  lazy val watchAddress = Set[String]()
}
