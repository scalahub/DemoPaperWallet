name := "PaperWallet"

version := "0.1"

scalaVersion := "2.12.8"

lazy val CryptoNode = RootProject(uri("git://github.com/scalahub/CryptoNode.git"))
//lazy val CryptoNode = RootProject(uri("../CryptoNode"))

lazy val root = (project in file(".")).dependsOn(CryptoNode).settings(
  name := "PaperWallet",
  mainClass in (Test, run) := Some("org.sh.cryptonode.TestPaperWallet"),
  mainClass in (Compile, run) := Some("org.sh.cryptonode.PaperWallet")
)

Project.inConfig(Test)(baseAssemblySettings)
