package org.bbk.gameserver

import java.net.Socket

class Captain(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {

  private val excudedPushMethods = Set(
    "pushData",
    "pushMessage",
    "pushStart",
    "pushLoss",
    "pushWin",
    "pushEvent"
  )

  override def pushData(): Unit = {
    this.getClass.getDeclaredMethods
      .filter(_.getName.startsWith("push"))
      .foreach { method =>
        try {
          if !(excudedPushMethods.contains(method.getName) || (method.getName.contains('$'))) then
            method.invoke(this)
        } catch {
          case e: Exception => println(s"Error invoking method ${method.getName}: ${e.getMessage}")
        }
      }
  }

  def pushHealth(): Unit = {
    pushMessage(s"#health:${Ship.health.value}:${Ship.health.max}")
  }
  def pushShield(): Unit = {
    pushMessage(s"#shield:${Ship.shield}")
  }
  def pushWeapons(): Unit = {
    pushMessage(s"#weapons:${Ship.weapons}")
  }
  def pushRepaircolor(): Unit = {
    pushMessage(s"#repaircolor:${Ship.repairColor}")
  }
  def pushEnergy(): Unit = {
    pushMessage(s"#energy:${Ship.energy.value}:${Ship.energy.max}")
  }
  def pushEvent(eventType: EventType): Unit = {
    pushMessage(s"#event:${eventType}")
  }
  def pushShipSpeed(): Unit = {
    pushMessage(s"#shipSpeed:${Ship.shipSpeed.value}:${Ship.shipSpeed.max}")
  }
  def pushCoreAir(): Unit = {
    pushMessage(s"#coreAir:${Ship.coreAir.value}:${Ship.coreAir.max}")
  }
  def pushAirSupply(): Unit = {
    pushMessage(s"#airSupply:${Ship.airSupply}")
  }
  def pushRepairPoints(): Unit = {
    pushMessage(s"#repairPoints:${Ship.repairPoints}")
  }
  def pushDrive(): Unit = {
    pushMessage(s"#drive:${Ship.drive}")
  }
}