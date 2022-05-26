resolvers += "SonaType Snapshots s01" at "https://s01.oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies += "io.github.scalahub" %% "paperwallet" % "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "DemoPaperWallet",
    mainClass in (Compile, run) := Some("org.sh.cryptonode.DemoPaperWallet")
  )
