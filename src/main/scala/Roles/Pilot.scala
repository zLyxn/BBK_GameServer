package org.bbk.gameserver

import java.net.Socket

class Pilot(socket: Socket) extends Client(socket) {
  override def handleRoleCommands(parts: Array[String]): String = {
    parts.head match {
      case "#hitmeteor" if parts.length == 2 => hitMeteor(Color.toColor(parts(1))); ""
      case _ => super.handleRoleCommands(parts)
    }
  }
  override def pushData(): Unit = {
    pushShipSpeed()
    pushMeteorAmount()
  }


  def hitMeteor(meteorColor: Color): Unit = {
    if (Ship.repairColor == meteorColor) {
      Ship.Energy += Config.Ship.ENERGY_GAIN
    } else {
      Ship.Health -= (Config.Ship.DAMAGE * (1 - (if Ship.Shield then Config.Ship.RESISTANCE else 0))).toInt
    }
  }
  
  def pushShipSpeed(): Unit = {
    pushMessage(s"#shipSpeed:${Ship.ShipSpeed.value}")
  }
  def pushMeteorAmount(): Unit = {
    pushMessage(s"#meteorAmount:${Ship.meteorAmount}")
  }
}