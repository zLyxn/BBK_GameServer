package org.bbk.gameserver

import java.net.Socket

class Pilot(socket: Socket) extends Client(socket) {
  override def handleRoleCommands(parts: Array[String]): String = {
    parts.head match {
      case "#hitmeteor" if parts.length == 2 => hitMeteor(Color.toColor(parts(1))); ""
    }
  }


  def hitMeteor(meteorColor: Color): Unit = {
    if (Ship.repairColor == meteorColor) {
      // positiv: + neue Energie
    } else {
      Ship.Health -= (Ship.DAMAGE * (1 - (if Ship.Shield then Ship.RESISTANCE else 0))).toInt
    }
  }
}