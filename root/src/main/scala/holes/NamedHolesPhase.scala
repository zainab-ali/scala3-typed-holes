package holes

import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.typer.FrontEnd

class NamedHolesPhase extends PluginPhase {

  val phaseName: String = "named-holes"
  override val runsAfter: Set[String] = Set(FrontEnd.name)
  override val runsBefore: Set[String] = Set(???)

}
