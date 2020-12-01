import mill._
import mill.scalalib._
import java.io.File

object root extends ScalaModule {
  def scalaVersion = "3.0.0-M2"
  def ivyDeps = Agg(
    ivy"org.scala-lang::scala3-compiler:3.0.0-M2",
  )

  object test extends Tests {
    def forkArgs = T {
      val classpath = root.scalaCompilerClasspath()
        .map(_.path)
        .mkString(File.pathSeparator)
      Seq(s"-Dplugin.jar=${root.jar().path}", s"-Dscalac.classpath=$classpath")
    }

    def ivyDeps = Agg(ivy"org.scalameta::munit::0.7.19")
    def testFrameworks = Seq("munit.Framework")
  }
}
