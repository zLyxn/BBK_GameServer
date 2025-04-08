package org.bbk.gameserver

import java.net.Socket

class Engineer(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine, false) {
  override def handleRoleCommands(parts: Array[String]): String = {
    parts.head match {
      case "#shield" if parts.length == 2 => setShieldState(parts(1).toBoolean); ""
      case "#weapons" if parts.length == 2 => setWeaponsState(parts(1).toBoolean); ""
      case "#airSupply" if parts.length == 2 => setAirSupplyState(parts(1).toBoolean); ""
      case "#drive" if parts.length == 2 => setDrive(parts(1).toBoolean); ""
      case "#repair" if parts.length == 2 => repair(parts(1)); ""
      case _ => super.handleRoleCommands(parts)
    }
  }

  private def setWeaponsState(state: Boolean): Unit = {
    Ship.weapons = state
  }
  private def setAirSupplyState(state: Boolean): Unit = {
    Ship.airSupply = state
    if(!state){
      gameEngine.findRole(classOf[Captain]).foreach(_.pushAirSupply())
    }
  }

  override def pushData(): Unit = {

  }
  private def setShieldState(state: Boolean): Unit = {
    if(Events.getActiveEvents.exists(_.isInstanceOf[ShieldDownEvent])){
      Ship.shield = state
      gameEngine.findRole(classOf[Captain]).foreach(_.pushShield())
    }
    gameEngine.logger.trace(s"Ship.Shield: ${Ship.shield}")
  }
  private def setDrive(state: Boolean): Unit = {
    Ship.drive = state
  }

  private def repair(system: String): Unit = {
    Ship.repairPoints = Ship.repairPoints - 1
    gameEngine.sendCaptainMessage(_.pushRepairPoints())

    val decapitalizedSystem = gameEngine.decapitalize(system)

    val workingField = Ship.getClass.getDeclaredField(decapitalizedSystem + "Working")
    workingField.setAccessible(true)
    workingField.setBoolean(Ship, true)

    gameEngine.sendCaptainMessage({ captain =>
      captain.getClass.getDeclaredMethod("push" + system).invoke(captain)
    })
  }
}