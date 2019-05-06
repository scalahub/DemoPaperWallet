package org.sh.cryptonode

import java.io.File
import java.nio.file.Files
import org.sh.cryptonode.btc._
import org.sh.cryptonode.ecc.Util._
import scala.collection.JavaConversions._
import org.sh.cryptonode.util._
import org.sh.cryptonode.btc.BitcoinUtil._
import org.sh.cryptonode.util.BytesUtil._
import org.sh.cryptonode.util.StringUtil._
import org.sh.cryptonode.util.BigIntUtil._

// add more vectors from https://motls.blogspot.in/2017/12/bitcoin-block-without-valid-miner-fee.html

object TestBlockParser extends App {
  import BlockParserTestVectors._  
  Seq(    
    blk1Vector,
    blk2Vector,
    blk3Vector,
    blk4Vector,
    blk5Vector // genesis block
  ).foreach{
    case (hex, hash, mroot, json, size) =>
      new TestBlockParser(hex, hash, mroot, json, size)
  }
  println("All block parser tests passed.")
}

object BlockParserTestVectors {
  //  block is around 750 kb, too big to store in code, so read raw hex from file
  val blk1FileRaw = "000000000000000001f942eb4bfa0aeccb6a14c268f4c72d5fff17270da771b9.txt" // from https://blockchain.info/block/000000000000000001f942eb4bfa0aeccb6a14c268f4c72d5fff17270da771b9?format=hex
  //  block is around 750 kb, too big to store in code, so read json from file
  val blk1FileJson = "000000000000000001f942eb4bfa0aeccb6a14c268f4c72d5fff17270da771b9.json.txt" // https://chainquery.com/bitcoin-api/getblock/000000000000000001f942eb4bfa0aeccb6a14c268f4c72d5fff17270da771b9/true
  val blk1Json = Files.readAllLines(new File(ResourceUtil.getFileLocation(blk1FileJson)).toPath).mkString
  val blk1Raw = Files.readAllLines(new File(ResourceUtil.getFileLocation(blk1FileRaw)).toPath).mkString.trim
  val blk1Size = 749157   //  found from bitcoind api
  val blk1Hash = "000000000000000001f942eb4bfa0aeccb6a14c268f4c72d5fff17270da771b9"
  val blk1MRoot = "9b7d5896398581a7ff26be4b3684ddd95a7c1dc1aab1df37cbb2127379ae8584"

  // following is an unusual block, probably mined due to a bug in the miner's software
  // https://motls.blogspot.in/2017/12/bitcoin-block-without-valid-miner-fee.html
  // https://bitcointalk.org/index.php?topic=2667744.0 
  val blk2Raw = "00000020bb26f9994bd6d4d51f95c90fe9ae7c0b4fd6d2b882532b0000000000000000008211922c4f11084ef2e9cc4d858ad0f4e1a911ae1740e5a1bf3b823a3b85f89bb88c475a45960018dd9722970101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff1203dea707055a478cb801b80100006ea50000ffffffff0100000000000000002952534b424c4f434b3addbf517adf8ffd4bca7751505b39c9013a0d1fd479fc4e901b39dd57b347c62400000000"
  // following from bitcoind getblock command
  val blk2Json = """
{
	"result": {
		"hash": "0000000000000000004b27f9ee7ba33d6f048f684aaeb0eea4befd80f1701126",
		"confirmations": 179,
		"strippedsize": 200,
		"size": 200,
		"weight": 800,
		"height": 501726,
		"version": 536870912,
		"versionHex": "20000000",
		"merkleroot": "9bf8853b3a823bbfa1e54017ae11a9e1f4d08a854dcce9f24e08114f2c921182",
		"tx": [
			"9bf8853b3a823bbfa1e54017ae11a9e1f4d08a854dcce9f24e08114f2c921182"
		],
		"time": 1514638520,
		"mediantime": 1514634783,
		"nonce": 2535626717,
		"bits": "18009645",
		"difficulty": 1873105475221.611,
		"chainwork": "000000000000000000000000000000000000000000d9219e7651a5dea9b0344d",
		"previousblockhash": "0000000000000000002b5382b8d2d64f0b7caee90fc9951fd5d4d64b99f926bb",
		"nextblockhash": "0000000000000000008b28f1726cc181500c7898a63689a9c9e723643e593261"
	},
	"error": null,
	"id": null
}"""
  val blk2Hash = "0000000000000000004b27f9ee7ba33d6f048f684aaeb0eea4befd80f1701126"
  val blk2MRoot = "9bf8853b3a823bbfa1e54017ae11a9e1f4d08a854dcce9f24e08114f2c921182"
  val blk2Size = 200
  
  // added 3rd test vector from 00000000000000000013460c16ffa09553e9739aebbd467d7bf34284f2dd5134
  val blk3Raw = "000000206132593e6423e7c9a98936a698780c5081c16c72f1288b000000000000000000919f163a390470cea492699b1724498ce80f5f3727bd045f0ec9dc5da4a536d2488f475a459600188f98e13a01010000000001010000000000000000000000000000000000000000000000000000000000000000ffffffff2403e0a707174d696e656420627920416e74506f6f6c6e06205a478f47b1080000433c8ee3ffffffff02807c814a000000001976a914eb181d60731cc7521db83a8f93beba8da692a22988ac0000000000000000266a24aa21a9ede2f61c3f71d1defd3fa999dfa36953755c690689799962b48bebd836974e8cf90120000000000000000000000000000000000000000000000000000000000000000000000000"
  // following from bitcoind getblock command
  val blk3Json = """
{
	"result": {
		"hash": "00000000000000000013460c16ffa09553e9739aebbd467d7bf34284f2dd5134",
		"confirmations": 200,
		"strippedsize": 249,
		"size": 285,
		"weight": 1032,
		"height": 501728,
		"version": 536870912,
		"versionHex": "20000000",
		"merkleroot": "d236a5a45ddcc90e5f04bd27375f0fe88c4924179b6992a4ce7004393a169f91",
		"tx": [
			"d236a5a45ddcc90e5f04bd27375f0fe88c4924179b6992a4ce7004393a169f91"
		],
		"time": 1514639176,
		"mediantime": 1514637171,
		"nonce": 987863183,
		"bits": "18009645",
		"difficulty": 1873105475221.611,
		"chainwork": "000000000000000000000000000000000000000000d92506b54c9204cec14973",
		"previousblockhash": "0000000000000000008b28f1726cc181500c7898a63689a9c9e723643e593261",
		"nextblockhash": "00000000000000000036141efd9a3c9c15f21320005782788276a6944aa8b4af"
	},
	"error": null,
	"id": null
}
"""
  val blk3Hash = "00000000000000000013460c16ffa09553e9739aebbd467d7bf34284f2dd5134"
  val blk3MRoot = "d236a5a45ddcc90e5f04bd27375f0fe88c4924179b6992a4ce7004393a169f91"
  val blk3Size = 285
    
  val blk4Raw = "02000000c65a467c95b9bef6c9021193cd974efbd68202021006a51900000000000000000d48586b5e290296f0ef7024f305b9b3a5c1cf24fb902f27ea0335088ef53b8498be91547cdd1b183e1612960101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff5603801b05e4b883e5bda9e7a59ee4bb99e9b1bcfabe6d6d1826d7955e515df6d4587632a80d1fc2bb8c965dc0d9f4417fefb7c328d0bcd6100000000000000000dde1bec70100004d696e6564206279207a6470777370ffffffff0100f90295000000001976a914c825a1ecf2a6830c4401620c3a16f1995057c2ab88ac00000000"
  val blk4Json = """{
	"result": {
		"hash": "00000000000000000429a0c4fbe735b2d8b493daedf0207728543f748c262437",
		"confirmations": 167955,
		"strippedsize": 252,
		"size": 252,
		"weight": 1008,
		"height": 334720,
		"version": 2,
		"versionHex": "00000002",
		"merkleroot": "843bf58e083503ea272f90fb24cfc1a5b3b905f32470eff09602295e6b58480d",
		"tx": [
			"843bf58e083503ea272f90fb24cfc1a5b3b905f32470eff09602295e6b58480d"
		],
		"time": 1418837656,
		"mediantime": 1418833238,
		"nonce": 2517767742,
		"bits": "181bdd7c",
		"difficulty": 39457671307.13873,
		"chainwork": "0000000000000000000000000000000000000000000368b0fe25403bc5a713a5",
		"previousblockhash": "000000000000000019a50610020282d6fb4e97cd931102c9f6beb9957c465ac6",
		"nextblockhash": "00000000000000001b98a116e9b67465a8fb11ee15c5173ae772d6837e2abb1b"
	},
	"error": null,
	"id": null
}"""
  val blk4Hash = "00000000000000000429a0c4fbe735b2d8b493daedf0207728543f748c262437"
  val blk4MRoot = "843bf58e083503ea272f90fb24cfc1a5b3b905f32470eff09602295e6b58480d"
  val blk4Size = 252
  
  // genesis block below
  val blk5Raw = "0100000000000000000000000000000000000000000000000000000000000000000000003BA3EDFD7A7B12B27AC72C3E67768F617FC81BC3888A51323A9FB8AA4B1E5E4A29AB5F49FFFF001D1DAC2B7C0101000000010000000000000000000000000000000000000000000000000000000000000000FFFFFFFF4D04FFFF001D0104455468652054696D65732030332F4A616E2F32303039204368616E63656C6C6F72206F6E206272696E6B206F66207365636F6E64206261696C6F757420666F722062616E6B73FFFFFFFF0100F2052A01000000434104678AFDB0FE5548271967F1A67130B7105CD6A828E03909A67962E0EA1F61DEB649F6BC3F4CEF38C4F35504E51EC112DE5C384DF7BA0B8D578A4C702B6BF11D5FAC00000000"
  val blk5Json = """{
	"result": {
		"hash": "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",
		"confirmations": 503395,
		"strippedsize": 285,
		"size": 285,
		"weight": 1140,
		"height": 0,
		"version": 1,
		"versionHex": "00000001",
		"merkleroot": "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b",
		"tx": [
			"4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"
		],
		"time": 1231006505,
		"mediantime": 1231006505,
		"nonce": 2083236893,
		"bits": "1d00ffff",
		"difficulty": 1,
		"chainwork": "0000000000000000000000000000000000000000000000000000000100010001",
		"nextblockhash": "00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048"
	},
	"error": null,
	"id": null
}"""
  val blk5Hash = "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f"
  val blk5MRoot = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b".toLowerCase
  val blk5Size = 285
  
  val blk1Vector = (
    blk1Raw, // raw hex bytes
    blk1Hash, // expected blk hash
    blk1MRoot, // expected merkle root
    blk1Json, // json from bitcoind
    blk1Size // size reported in Json
  )

  val blk2Vector = (
    blk2Raw,
    blk2Hash,
    blk2MRoot,
    blk2Json,
    blk2Size
  )

  val blk3Vector = (
    blk3Raw,
    blk3Hash,
    blk3MRoot,
    blk3Json,
    blk3Size
  )
  
  val blk4Vector = (
    blk4Raw,
    blk4Hash,
    blk4MRoot,
    blk4Json,
    blk4Size
  )
  
  val blk5Vector = (
    blk5Raw,
    blk5Hash,
    blk5MRoot,
    blk5Json,
    blk5Size
  )
  
}
class TestBlockParser(hex:String, blkHash:String, merkleRoot:String, json:String, size:Long) {  
  val bytes = hex.decodeHex  
  require(bytes.size == size, s"Expected $size bytes. Found ${bytes.size} bytes")
  /////////////////////////////////// test that JSON and test vectors correspond
  val xml = Json2XML.jsonStringToXML(json)
  val xhash = (xml \\ "hash").text
  val xmerkleRoot = (xml \\ "merkleroot").text
  val xtxids = (xml \\ "tx").map(_.text)  
  require(xhash == blkHash)
  require(xmerkleRoot == merkleRoot, s"Merkle root mismatch. JSON contains: $xmerkleRoot, test vector contains: $merkleRoot")  
  /////////////////////////////////// done
  
  val parsedBlk = new BlockParser(bytes).getBlock // now test the parser

  assert(parsedBlk.header.hash == blkHash)
  val computedMerkleRoot = parsedBlk.computeMerkleRoot.toLowerCase
  assert(computedMerkleRoot == merkleRoot, s"Expected $merkleRoot. Found $computedMerkleRoot")
  
  val parsedTxs = parsedBlk.txs
  val parsedTxids = parsedTxs.map(_.txid) 

  assert(xtxids.size == parsedTxids.size)

  xtxids zip parsedTxids foreach{case (left, right) => assert (left == right)}
  
  parsedTxs zip xtxids foreach{
    case (tx, txid) =>  
      assert (new TxParser(tx.serialize).getTx.txid == txid)      
  }
  
  val parsedBytes = parsedBlk.serialize

  assert(parsedBytes.size == bytes.size)
  
  (parsedBytes zip bytes).zipWithIndex.foreach{
    case ((l, r), i) => assert(l == r, s"Left ($l) != Right ($r) at index $i")
  }
  println(s"Passed block $blkHash with ${parsedTxs.size} transactions")
}
