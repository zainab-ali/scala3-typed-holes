package holes

import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.transform.{PatternMatcher, PostTyper}
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.StdNames
import dotty.tools.dotc.report

class TypedHolesPhase extends PluginPhase {
  import dotty.tools.dotc.ast.tpd._

  val phaseName: String = "typed-holes"

  override val runsAfter: Set[String] = Set(PostTyper.name)
  override val runsBefore: Set[String] = Set(PatternMatcher.name)

  override def transformValDef(tree: ValDef)(using Context): Tree =
    tree match {
      case ValDef(_, _, Hole(hole)) =>
        log(hole, tree.tpt)
        tree
      case _ => tree
    }

  override def transformDefDef(tree: DefDef)(using Context): Tree =
    tree match {
      case DefDef(_, _, _, tpt, Hole(hole)) =>
        log(hole, tpt)
        tree
      case _ => tree
    }

  def log(hole: Tree, tpe: Tree)(using Context): Unit = {
    val message = s"Found hole with type: ${tpe.show}"
    report.warning(message, hole.srcPos)
  }

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
