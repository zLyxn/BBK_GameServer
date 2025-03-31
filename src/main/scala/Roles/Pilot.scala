package org.bbk.gameserver

import java.net.Socket
import scala.util.Random

class Pilot(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {
  override def handleRoleCommands(parts: Array[String]): String = {
    parts.head match {
      case "#hitmeteor" if parts.length == 2 => hitMeteor(Color.fromString(parts(1))); ""
      case _ => super.handleRoleCommands(parts)
    }
  }
  override def pushData(): Unit = {
    pushShipSpeed()
    pushMeteorAmount()
  }


  private def hitMeteor(meteorColor: Color): Unit = {
    if (Ship.repairColor == meteorColor) {
      Ship.energy += Config.Ship.ENERGY_GAIN
      Ship.repairPointChance += Config.Game.REPAIRPOINTCHANCEGAIN
    } else {
      Ship.health -= (Config.Ship.DAMAGE * (1 - (if Ship.shield then Config.Ship.RESISTANCE else 0))).toInt
      randomSystemDown()
    }
  }
  
  def pushShipSpeed(): Unit = {
    if(Ship.drive && Ship.driveWorking){
      pushMessage(s"#shipSpeed:${Ship.shipSpeed.value}:${Ship.shipSpeed.max}")
    }
    pushMessage(s"#shipSpeed:0:${Ship.shipSpeed.max}")
  }
  private def pushMeteorAmount(): Unit = {
    pushMessage(s"#meteorAmount:${Ship.meteorAmount}")
  }

  private def randomSystemDown(): Unit = {
    if (Random.nextInt(100) < Config.Game.SYSTEMDOWNCHANCE) {
      randomSystem() match {
        case System.Shield => updateShipSystem(Ship.shield = false, Ship.shieldWorking = false, _.pushShield())
        case System.Weapons => updateShipSystem(Ship.weapons = false, Ship.weaponsWorking = false, _.pushWeapons())
        case System.AirSupply => updateShipSystem(Ship.airSupply = false, Ship.shieldWorking = false, _.pushAirSupply())
        case System.Drive => updateShipSystem(Ship.drive = false, Ship.driveWorking = false, _.pushDrive())
      }
    }
  }

  private def randomSystem(): System = {
    System.values(Random.nextInt(System.values.length))
  }

  private def updateShipSystem(systemUpdate: => Unit, systemWorkingUpdate: => Unit, notification: Captain => Unit): Unit = {
    systemUpdate
    systemWorkingUpdate
    gameEngine.findRole(classOf[Captain]).foreach(notification)
  }
}