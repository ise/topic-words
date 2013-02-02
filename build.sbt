name := "topic-words"

organization := "com.under_hair"

version := "0.0.1"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "commons-daemon" % "commons-daemon" % "1.0.10",
  "commons-lang" % "commons-lang" % "2.6",
  "org.specs2" %% "specs2" % "1.13" % "test",
  "joda-time" % "joda-time" % "2.1",
  "org.joda" % "joda-convert" % "1.2",
  "org.apache.hadoop" % "hadoop-common" % "0.23.5",
  "org.apache.hadoop" % "hadoop-hdfs" % "0.23.5",
  "org.apache.mahout" % "mahout-core" % "0.7"
)

