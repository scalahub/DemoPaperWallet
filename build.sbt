libraryDependencies += "io.github.scalahub" %% "cryptogram" % "1.1"

ThisBuild / version := "1.0"

// resolvers += "Sonatype Releases" at "https://s01.oss.sonatype.org/content/repositories/releases"

lazy val root = (project in file("."))
  .settings(
    name := "DemoPaperWallet",
    mainClass in (Compile, run) := Some("org.sh.cryptonode.DemoPaperWallet")
  )
assembly / artifact := {
  val art = (assembly / artifact).value
  //  art.withClassifier(Some("assembly"))
  art.withClassifier(None)
}

addArtifact(assembly / artifact, assembly)

//Compile / packageSrc / publishArtifact := false
//
//Compile / packageDoc / publishArtifact := false
//
Compile / packageBin / publishArtifact := false
