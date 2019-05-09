name := "CryptoNode"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.8"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"

// https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on
libraryDependencies += "org.bouncycastle" % "bcprov-jdk15on" % "1.61"

libraryDependencies += "commons-codec" % "commons-codec" % "1.12"

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.6"

// https://mvnrepository.com/artifact/org.json/json
libraryDependencies += "org.json" % "json" % "20140107"

// TESTING LIBS
// https://mvnrepository.com/artifact/org.bitcoinj/bitcoinj-core
libraryDependencies += "org.bitcoinj" % "bitcoinj-core" % "0.14.7" % Test

// https://stackoverflow.com/a/34910525/243233
Project.inConfig(Test)(baseAssemblySettings)
// assemblyJarName in (Test, assembly) := s"${name.value}-test-${version.value}.jar"