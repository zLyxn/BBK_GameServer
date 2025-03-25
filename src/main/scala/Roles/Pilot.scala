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
    } else {
      Ship.health -= (Config.Ship.DAMAGE * (1 - (if Ship.shield then Config.Ship.RESISTANCE else 0))).toInt
      // ein zufälliges System kaputt
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
    // TODO: Systeme nicht ausschalten SONDERN extra wert für kaputte Systeme implementieren
    if (Random.nextInt(100) < Config.Game.SYSTEMDOWNCHANCE) {
      randomSystem() match {
        case "shield" => updateShipSystem(Ship.shield = false, Ship.shieldWorking, _.pushShield())
        case "weapons" => updateShipSystem(Ship.weapons = false, Ship.weaponsWorking, _.pushWeapons())
        case "airSupply" => updateShipSystem(Ship.airSupply = false, Ship.shieldWorking, _.pushAirSupply())
        case "drive" => updateShipSystem(Ship.drive = false, Ship.driveWorking, _.pushDrive())
      }
    }
  }

  private def randomSystem(): String = {
    //TODO: Systeme enum HIER
    Random.nextInt(4) match {
      case 0 => "shield"
      case 1 => "weapons"
      case 2 => "airSupply"
      case 3 => "drive"
    }
  }

  private def updateShipSystem(systemUpdate: => Unit, systemWorkingUpdate: => Unit, notification: Captain => Unit): Unit = {
    systemUpdate
    systemWorkingUpdate
    gameEngine.findRole(classOf[Captain]).foreach(notification)
  }
}