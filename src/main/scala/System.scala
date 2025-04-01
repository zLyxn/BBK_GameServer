package org.bbk.gameserver

enum System {
  case Shield
  case Weapons
  case AirSupply
  case Drive
}
object System {
  def fromString(system: String): System = {
    val systems = System.values
    val foundSystem = systems.find(_.toString.equalsIgnoreCase(system))
    if foundSystem.isDefined then
      foundSystem.get
    else
      throw new IllegalArgumentException(s"Unknown system: $system")
  }
}