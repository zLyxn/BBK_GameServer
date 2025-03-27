package org.bbk.gameserver

object Ship {
  val health = new Stat(100, 100)
  val energy = new Stat(100, 100)
  val shipSpeed = new Stat(100, 100)
  val coreAir = new Stat(100, 100) // TODO: send Captain + debug
  
  var meteorAmount: Int = 0
  var repairColor: Color = Color.None
  var friendlyColor: Color = Color.Blue
  var ammo: Int = 50
  var shield: Boolean = false
  var weapons: Boolean = true
  var airSupply: Boolean = true // TODO: send Captain + debug
  var drive: Boolean = true

  var shieldWorking: Boolean = false
  var weaponsWorking: Boolean = true
  var airSupplyWorking: Boolean = true
  var driveWorking: Boolean = true
  
  var repairPoints: Int = 0

  override def toString: String = {
    val fields = this.getClass.getDeclaredFields.filterNot(_.getName.contains("$"))
    fields.map { field =>
      field.setAccessible(true)
      s"${field.getName}: ${field.get(this)}"
    }.mkString("\r\n").strip()
  }

  enum Systems {
    case Shield
    case Weapons
    case AirSupply
    case Drive
  }

  //TODO: viellecih andere Position
  def toSystems(system: String): Systems = {
    system.toLowerCase match {
      case "shield" => Systems.Shield
      case "weapons" => Systems.Weapons
      case "airsupply" => Systems.AirSupply
      case "drive" => Systems.Drive
      case _ => throw new IllegalArgumentException(s"Unknown system: $system")
    }
  }
}
