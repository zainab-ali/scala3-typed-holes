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
      case ValDef(_, _, Ident(name)) if name == StdNames.nme.`???` =>
        report.warning("Found a hole", tree.srcPos)
        tree
      case _ => tree
    }
}
