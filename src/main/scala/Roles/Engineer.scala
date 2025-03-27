package org.bbk.gameserver

import java.net.Socket

class Engineer(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {
  override def handleRoleCommands(parts: Array[String]): String = {
    parts.head match {
      case "#shield" if parts.length == 2 => setShieldState(parts(1).toBoolean); ""
      case "#weapons" if parts.length == 2 => setWeaponsState(parts(1).toBoolean); ""
      case "#airSupply" if parts.length == 2 => setAirSupplyState(parts(1).toBoolean); ""
      case "#shipSpeed" if parts.length == 2 => setShipSpeed(parts(1).toInt); ""
      case "#repair" if parts.length == 2 => repair(parts(1).toLowerCase); ""
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
    println(s"Ship.Shield: ${Ship.shield}")
  }
  private def setShipSpeed(speed: Int): Unit = {
    Ship.shipSpeed.value = speed
  }

  private def repair(system: String): Unit = {
    val systemEnum = Ship.toSystems(system)
    systemEnum match {
      case Ship.Systems.Shield =>
        Ship.shield = true
        sendCaptainMessage(_.pushShield())
      case Ship.Systems.Weapons =>
        Ship.weapons = true
        sendCaptainMessage(_.pushWeapons())
        gameEngine.findRole(classOf[WeaponsOfficer]).foreach(_.pushWeapons())
      case Ship.Systems.AirSupply =>
        Ship.airSupply = true
        sendCaptainMessage(_.pushAirSupply())
      case Ship.Systems.Drive =>
        Ship.drive = true
        sendCaptainMessage(_.pushDrive())
    }
  }

  private def sendCaptainMessage(action: Captain => Unit): Unit = {
    gameEngine.findRole(classOf[Captain]).foreach(action)
  }
}