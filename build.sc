import mill._
import mill.scalalib._

object root extends ScalaModule {
  def scalaVersion = "3.0.0-M2"
  def ivyDeps = Agg(
    ivy"org.scala-lang::scala3-compiler:3.0.0-M2",
  )
}
