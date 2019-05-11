name := "CryptoNode"

version := "0.1"

scalaVersion := "2.12.8"

lazy val core = (project in file("core")).settings(
	libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.8",

	libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6",

	// https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on
	libraryDependencies += "org.bouncycastle" % "bcprov-jdk15on" % "1.61",

	libraryDependencies += "commons-codec" % "commons-codec" % "1.12",

	// https://mvnrepository.com/artifact/commons-io/commons-io
	libraryDependencies += "commons-io" % "commons-io" % "2.6",

	// https://mvnrepository.com/artifact/org.json/json
	libraryDependencies += "org.json" % "json" % "20140107",
	libraryDependencies += "org.bitcoinj" % "bitcoinj-core" % "0.14.7" % Test,
	name := "core",
	mainClass in (Test, run) := Some("org.sh.cryptonode.RunStandAloneTests")
)

lazy val bitcoind = (project in file("bitcoind")).dependsOn(core).settings(
	name := "bitcoind",
	mainClass in (Test, run) := Some("org.sh.cryptonode.btc.bitcoind.BitcoindTxParserTest")
)

lazy val paperwallet = (project in file("paperwallet")).dependsOn(core).settings(
	name := "paperwallet",
	mainClass in (Test, run) := Some("org.sh.cryptonode.TestPaperWallet"),
	mainClass in (Compile, run) := Some("org.sh.cryptonode.PaperWallet")
)

lazy val bch = (project in file("bch")).dependsOn(core).settings(
	// set the name of the project
	name := "bch",
	mainClass in (Test, run) := Some("org.sh.cryptonode.bch.TestUAHF")
)

lazy val root = (project in file(".")).aggregate(core,bch,paperwallet,bitcoind).settings(
	mainClass in (Test, run) := Some("org.sh.cryptonode.btc.BitcoinsNode"),
	name := "CryptoNode"
).dependsOn(
	core,bch,paperwallet,bitcoind
)

Project.inConfig(Test)(baseAssemblySettings)
