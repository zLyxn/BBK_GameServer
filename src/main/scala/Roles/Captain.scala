package org.bbk.gameserver

import java.net.Socket

class Captain(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {

  override def pushData(): Unit = {
    pushHealth()
    pushShield()
    pushRepaircolor()
    pushEnergy()
    pushShipSpeed()
    pushCoreAir()
    pushAirSupply()
    pushWeapons()
    pushRepairPoints()
    pushDrive()
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