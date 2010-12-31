import sbt._

class VisualAssertProject(info: ProjectInfo) extends DefaultProject(info)
{
  val dep = "org.scalatest" % "scalatest" % "1.2"
}
