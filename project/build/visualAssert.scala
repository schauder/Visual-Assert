import sbt._

class VisualAssertProject(info: ProjectInfo) extends DefaultProject(info)
{
  val scalatest = "org.scalatest" % "scalatest" % "1.2"
  val grizzled = "org.clapper" %% "grizzled-slf4j" % "0.3.2"
  val swing = "org.scala-lang" % "scala-swing" % "2.8.1"
}
