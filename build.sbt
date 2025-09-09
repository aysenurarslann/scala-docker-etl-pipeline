name := "CloudDataPipeline"
version := "0.1"
scalaVersion := "2.13.12"
libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % "1.12.681", // Bulut ortamı için hazırda kalsın
  "com.amazonaws" % "aws-java-sdk-redshift" % "1.12.681", // Bulut ortamı için hazırda kalsın
  "org.postgresql" % "postgresql" % "42.7.3" // PostgreSQL JDBC sürücüsü
)
Compile / mainClass := Some("pipeline.S3ToRedshiftETL")

Compile / scalaSource := baseDirectory.value / "src"
