package holes

import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.typer.FrontEnd
import dotty.tools.dotc.transform.{PatternMatcher, PostTyper}

class TypedHolesPhase extends PluginPhase {

  val phaseName: String = "typed-holes"
  override val runsAfter: Set[String] = Set(PostTyper.name)
  override val runsBefore: Set[String] = Set(PatternMatcher.name)
}
