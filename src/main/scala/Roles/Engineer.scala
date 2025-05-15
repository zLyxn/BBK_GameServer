package org.bbk.gameserver

import java.net.Socket

class Engineer(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {
  override def handleRoleCommands(parts: Array[String]): String = {
    parts.head match {
      case "#shield" if parts.length == 2 => setShieldState(parts(1).toBoolean); ""
      case "#weapons" if parts.length == 2 => setWeaponsState(parts(1).toBoolean); ""
      case "#airsupply" if parts.length == 2 => setAirSupplyState(parts(1).toBoolean); ""
      case "#drive" if parts.length == 2 => setDrive(parts(1).toInt); ""
      case "#repair" if parts.length == 2 => repair(parts(1)); ""
      case "#minigame" if parts.length == 2 => minigame(parts(1)); ""
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
  private def minigame(state: String): Unit = {
    state match {
      case "start" => Ship.energy -= Config.Ship.ENERGY_MINIGAME_LOSS
      case "win" => Ship.repairPoints += Config.Ship.REPAIRPOINTS_MINIGAME_GAIN
      case _ => gameEngine.logger.warn("Unknown minigame state: " + state)
    }
    gameEngine.sendCaptainMessage(_.pushRepairPoints())
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
  private def setDrive(value: Int): Unit = {
    Ship.drive.setValue(value)
  }

  private def repair(system: String): Unit = {
    
    if (!System.values.contains(System.fromString(system))) {
      gameEngine.logger.warn(s"Unknown system: $system")
      return
    }
    if (Ship.repairPoints <= 0){
      gameEngine.logger.warn("Not enough repair points")
      return
    }
    Ship.repairPoints = Ship.repairPoints - 1
    gameEngine.sendCaptainMessage(_.pushRepairPoints())

    val decapitalizedSystem = gameEngine.decapitalize(system)

    val workingField = Ship.getClass.getDeclaredField(decapitalizedSystem + "Working")
    workingField.setAccessible(true)
    workingField.setBoolean(Ship, true)

    gameEngine.sendCaptainMessage({ captain =>
      val _ = captain.getClass.getDeclaredMethod("push" + system).invoke(captain)
    })
  }
}