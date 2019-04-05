name := "transcribe"
version := "1.0"

scalaVersion := "2.12.8"

lazy val awsVersion = "1.11.531"

libraryDependencies ++= Seq(
  ws,
  "com.amazonaws" % "aws-java-sdk-core" % awsVersion,
  "com.gu" % "kinesis-logback-appender" % "1.3.0",
  "com.amazonaws" % "aws-java-sdk-ec2" % awsVersion,
  "net.logstash.logback" % "logstash-logback-encoder" % "4.2",
  "com.gu" %% "pan-domain-auth-play_2-6" % "0.7.0"
)
routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala, RiffRaffArtifact, JDebPackaging, SystemdPlugin)
  .settings(Defaults.coreDefaultSettings: _*)
  .settings(
    name in Universal := normalizedName.value,
    topLevelDirectory := Some(normalizedName.value),
    riffRaffPackageName := name.value,
    riffRaffManifestProjectName := s"editorial-tools:${name.value}",
//    riffRaffBuildIdentifier :=  Option(System.getenv("BUILD_NUMBER")).getOrElse("DEV"),
    riffRaffUploadArtifactBucket := Option("riffraff-artifact"),
    riffRaffUploadManifestBucket := Option("riffraff-builds"),
//    riffRaffManifestBranch := Option(System.getenv("BRANCH_NAME")).getOrElse("unknown_branch"),

    riffRaffPackageType := (packageBin in Debian).value,

    debianPackageDependencies := Seq("openjdk-8-jre-headless"),
    maintainer := "Editorial Tools <digitalcms.dev@guardian.co.uk>",
    packageSummary := "Transcribe",
    packageDescription := """A single place for atoms of all types""",

    riffRaffArtifactResources ++= Seq(
      baseDirectory.value / "cloudformation" / "Transcribe.yml" -> s"packages/cloudformation/Transcribe.yml"
    ),
    javaOptions in Universal ++= Seq(
      "-Dpidfile.path=/dev/null"
    )
  )