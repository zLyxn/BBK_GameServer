package org.bbk.gameserver

import java.net.Socket

class Captain(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {

  override def pushData(): Unit = {
    pushHealth()
    pushShield()
    pushRepaircolor()
    pushEnergy()
    pushShipSpeed()
  }

  private def pushHealth(): Unit = {
    pushMessage(s"health:${Ship.health.value}:${Ship.health.max}")
  }
  def pushShield(): Unit = {
    pushMessage(s"shield:${Ship.Shield}")
  }
  private def pushRepaircolor(): Unit = {
    pushMessage(s"repaircolor:${Ship.repairColor}")
  }
  private def pushEnergy(): Unit = {
    pushMessage(s"energy:${Ship.Energy.value}:${Ship.Energy.max}")
  }
  def pushEvent(eventType: EventType): Unit = {
    pushMessage(s"#event:${eventType}")
  }
  def pushShipSpeed(): Unit = {
    pushMessage(s"#shipSpeed:${Ship.ShipSpeed.value}:${Ship.ShipSpeed.max}")
  }
  def pushCoreAir(): Unit = {
    pushMessage(s"#coreAir:${Ship.CoreAir.value}:${Ship.CoreAir.max}")
  }
}