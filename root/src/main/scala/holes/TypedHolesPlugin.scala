package holes

import dotty.tools.dotc.plugins.{PluginPhase, StandardPlugin}

class TypedHolesPlugin extends StandardPlugin {
  val name: String = "typed-holes"
  val description: String =
    "Treat use of ??? as a hole and give a useful warning about it"

  // TODO: Pass this some options for the log level
  def init(options: List[String]): List[PluginPhase] = Nil
}
