package org.bbk.gameserver

import java.net.Socket

class WeaponsOfficer(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {
  
  private var friendlyFireCount: Int = 0
  
  override def handleRoleCommands(parts: Array[String]): String = {
    parts.head match {
      case "#shoot" if parts.length == 2 => shoot(Target.toTarget(parts(1), Color.None)); ""
      case "#shoot" if parts.length == 3 => shoot(Target.toTarget(parts(1), Color.fromString(parts(2)))); ""
      case _ => super.handleRoleCommands(parts)
    }
  }

  override def pushData(): Unit = {
    pushAmmo()
    pushWeapons()
  }

  private def shoot(target: Target): Unit = {
    if(Ship.ammo > 0){
      Ship.ammo = Ship.ammo - 1
      target match {
        case Target.Meteor => Ship.meteorAmount -= 1
        case Target.Ship(color) => hitShip(color)
        case Target.Lootbox => Ship.energy += Config.Ship.ENERGY_GAIN
        case Target.Enemy => enemyKilled()//TODO: Command mit WepponsOfficer absprechen
        case Target.None => ()
      }
    }
    pushAmmo()
  }

  private def pushAmmo(): Unit = {
    pushMessage(s"#ammo:${Ship.ammo}")
  }
  
  private def enemyKilled(): Unit = {
    gameEngine.sendPilotMessage(_.pushEnemyKill())
  }
  
  private def hitShip(color: Color): Unit = {
    if (color == Ship.friendlyColor) {
      if(friendlyFireCount <= Config.Game.FRIENDLYKILLALLOWED){
        friendlyFireCount += 1
      }else{
        gameEngine.gameover(Config.Game.Deathmessages.FRIENDLYFIRE)
      }
    }else{
      Ship.ammo = Ship.ammo + Config.Ship.AMMO_GAIN
    }
    Ship.shield = true
  }
  
  def pushWeapons(): Unit = {
    pushMessage(s"#weapons:${Ship.weapons}")
  }

  def pushNewEnemy(): Unit = {
    pushMessage(s"#newEnemy")
  }
}