package holes

import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.transform.{PatternMatcher, PostTyper}
import dotty.tools.dotc.core.Contexts.{Context, ctx}
import dotty.tools.dotc.core.Names.TermName
import dotty.tools.dotc.core.StdNames
import dotty.tools.dotc.core.Types.Type
import dotty.tools.dotc.report
import dotty.tools.dotc.util.SrcPos
import scala.collection.mutable.ArrayStack

class TypedHolesPhase extends PluginPhase {
  import dotty.tools.dotc.ast.tpd._

  val phaseName: String = "typed-holes"

  override val runsAfter: Set[String] = Set(PostTyper.name)
  override val runsBefore: Set[String] = Set(PatternMatcher.name)

  case class Binding(tpe: Type, pos: SrcPos)

  private val bindings: ArrayStack[Map[TermName, Binding]] = new ArrayStack

  override def transformValDef(tree: ValDef)(using Context): Tree =
    tree match {
      case ValDef(_, _, Hole(hole)) =>
        log(hole, tree.tpt)
        tree
      case _ => tree
    }

  override def prepareForDefDef(tree: DefDef)(using Context): Context = {
    val bs = tree.vparamss.flatten.map(param =>
      (param.name, Binding(param.tpt.tpe, param.srcPos)))
      .toMap
    bindings.push(bs)
    ctx
  }

  override def transformDefDef(tree: DefDef)(using Context): Tree =
    tree match {
      case DefDef(_, _, vparamss, tpt, Hole(hole)) =>
        log(hole, tpt)
      case _ => ()
    }
    bindings.pop()
    tree

  def log(hole: Tree, tpe: Tree)(using Context): Unit = {
    val bindingStr: String =
      bindings.foldLeft(Map.empty[TermName, Binding]) {
        case (acc, level) => level ++ acc
      }.toList
        .sortBy(_._1.show).map {
          case (name, Binding(tpe, pos)) => s"  ${name.show}: ${tpe.show} (bound at ${posSummary(pos)})"
        }.mkString("\n")

    val message =
      if (bindingStr.nonEmpty)
        s"Bindings $bindingStr"
      else
        s"Found hole with type: ${tpe.show}"
    report.warning(message, hole.srcPos)
  }

  private def posSummary(pos: SrcPos)(using Context): String =
    s"${pos.sourcePos.source.file.name}:${pos.line}:${pos.sourcePos.column}"

  object Hole {
    def unapply(tree: Tree)(using Context): Option[Tree] = tree match {
      case _ if tree.symbol.name == StdNames.nme.`???` =>
        Some(tree)
      case Block(_, expr) if expr.symbol.name == StdNames.nme.`???` =>
        Some(expr)
      case _ =>
        None
    }
  }
}
