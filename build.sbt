ThisBuild / scalaVersion           := "2.13.18"
ThisBuild / crossScalaVersions     := Seq("2.13.18", "3.3.7")
ThisBuild / organization           := "com.permutive"
ThisBuild / versionPolicyIntention := Compatibility.BinaryCompatible

addCommandAlias("ci-test", "fix --check; versionPolicyCheck; mdoc; publishLocal; +test")
addCommandAlias("ci-docs", "github; mdoc; headerCreateAll")
addCommandAlias("ci-publish", "versionCheck; github; ci-release")

lazy val documentation = project.enablePlugins(MdocPlugin)

lazy val `doobie-pureconfig` = module
  .settings(libraryDependencies += "org.tpolecat" %% "doobie-core" % "1.0.0-RC12")
  .settings(libraryDependencies += "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC12")
  .settings(libraryDependencies ++= scalaVersion.value.on(3)(`pureconfig-generic-scala3`))
  .settings(libraryDependencies ++= scalaVersion.value.on(2)(`pureconfig-generic`))
  .settings(libraryDependencies += "org.typelevel" %% "munit-cats-effect" % "2.1.0" % Test)
  .settings(Test / fork := true)

//////////////////
// Dependencies //
//////////////////

def `pureconfig-generic-scala3` = "com.github.pureconfig" %% "pureconfig-generic-scala3" % "0.17.10"

def `pureconfig-generic` = "com.github.pureconfig" %% "pureconfig-generic" % "0.17.10"
