package holes

import munit._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

import scala.sys.process._

class IntegrationTest extends FunSuite {

  val pluginJar = sys.props("plugin.jar")
  val scalacClasspath = sys.props("scalac.classpath")
  val tastyDir = Files.createTempDirectory("scala3-typed-holes")

  runScalac("-version")

  val scenarios = Paths
    .get("./root/test/resources")
    .toFile
    .listFiles
    .toList
    .map(_.toPath)

  scenarios.foreach(testScenario)

  private def testScenario(scenario: Path): Unit = {
    test(scenario.getFileName.toString) {
      val result = runScalac(
        s"-Xplugin:$pluginJar",
        scenario.resolve("input.scala").toString,
        "-d",
        tastyDir.toString
      )
      val expected = new String(
        Files.readAllBytes(scenario.resolve("expected.txt")),
        StandardCharsets.UTF_8
      ).trim
      assertNoDiff(result, expected)
    }
  }

  private def runScalac(args: String*)(implicit loc: Location): String = {
    val buf = new StringBuffer
    val logger = new ProcessLogger {
      override def out(s: => String): Unit = { buf.append(s); buf.append('\n') }
      override def err(s: => String): Unit = { buf.append(s); buf.append('\n') }
      override def buffer[T](f: => T): T = f
    }

    val status = Process(
      "java"
        :: "-Dscala.usejavacp=true"
        :: "-cp" :: scalacClasspath
        :: "dotty.tools.dotc.Main"
        :: args.toList
    ).!(logger)
    assertEquals(status, 0)

    buf.toString
  }
}
