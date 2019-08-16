lazy val PaperWallet = RootProject(uri("git://github.com/scalahub/PaperWallet.git"))
//lazy val PaperWallet = RootProject(uri("../PaperWallet"))

lazy val root = (project in file(".")).dependsOn(PaperWallet).settings(
  name := "DemoPaperWallet",
  mainClass in (Compile, run) := Some("org.sh.cryptonode.DemoPaperWallet")
)