
package org.sh.cryptonode

import org.sh.cryptonode.btc.bitcoind.blockchain.BlockChainParser


object TestBlockChainParser extends BlockChainParser {
  lazy val watchAddress = Set[String]()
}
