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
    pushDrive()
  }


  private def hitMeteor(meteorColor: Color): Unit = {
    gameEngine.logger.debug(s"Hit meteor: ${meteorColor}:${Ship.repairColor == meteorColor}")
    if (Ship.repairColor == meteorColor) {
      Ship.energy += Config.Ship.ENERGY_GAIN
      Ship.repairPointChance += Config.Game.REPAIRPOINTCHANCEGAIN
    } else {
      Ship.health -= (Config.Ship.DAMAGE * (1 - (if Ship.shield then Config.Ship.RESISTANCE else 0))).toInt
      randomSystemDown()
    }
  }
  @Deprecated
  def pushShipSpeed(): Unit = {
      pushMessage(s"#shipSpeed:${Ship.shipSpeed.value}:${Ship.shipSpeed.max}")
  }
  private def pushMeteorAmount(): Unit = {
    pushMessage(s"#meteorAmount:${Ship.meteorAmount}")
  }
  def pushNewEnemy(): Unit = {
    pushMessage(s"#newEnemy")
  }
  def pushEnemyKill(): Unit = {
    pushMessage(s"#enemyKilled")
  }
  def pushDrive(): Unit = {
    pushMessage(s"#drive:${Ship.drive.value}:${Ship.drive.max}")
  }

  private def randomSystemDown(): Unit = {
    if (Random.nextInt(100) < Config.Game.SYSTEMDOWNCHANCE) {
      gameEngine.logger.trace("one System down")
      val system = randomSystem()

      val decapitalizedSystem = gameEngine.decapitalize(system.toString)

      val field = Ship.getClass.getDeclaredField(decapitalizedSystem)
      field.setAccessible(true)
      field.setBoolean(Ship, false)

      val workingField = Ship.getClass.getDeclaredField(decapitalizedSystem + "Working")
      workingField.setAccessible(true)
      workingField.setBoolean(Ship, false)

      gameEngine.sendCaptainMessage({ captain =>
        val _ = captain.getClass.getDeclaredMethod("push" + system.toString).invoke(captain)
      })
    }
  }

  private def randomSystem(): System = {
    System.values(Random.nextInt(System.values.length))
  }
}