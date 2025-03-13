package org.bbk.gameserver

import java.net.Socket

class WeaponsOfficer(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {
  
  private var friendlyFireCount: Int = 0
  
  override def handleRoleCommands(parts: Array[String]): String = {
    parts.head match {
      case "#hit" if parts.length == 2 => hit(Target.toTarget(parts(1), Color.None)); ""
      case "#hit" if parts.length == 3 => hit(Target.toTarget(parts(1), Color.toColor(parts(2)))); ""
      case _ => super.handleRoleCommands(parts)
    }
  }

  override def pushData(): Unit = {
// pushMunitionAmount()
  }

  private def hit(target: Target): Unit = {
    target match {
      case Target.Meteor => Ship.meteorAmount -= 1
      case Target.Ship(color) => Ship.Shield = true
      case Target.Lootbox => Ship.Energy += Config.Ship.ENERGY_GAIN
      case Target.None => ()
    }
  }
  private def hitShip(color: Color): Unit = {
    if (color == Ship.friendlyColor) {
      if(friendlyFireCount <= 3){
        friendlyFireCount += 1
      }else{
        gameEngine.gameover("Friendly Fire")
      }
    }else{
      Ship.ammo = Ship.ammo + Config.Ship.AMMO_GAIN
    }
    Ship.Shield = true
  }
}