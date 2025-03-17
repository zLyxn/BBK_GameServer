package org.bbk.gameserver

import java.net.Socket

class Captain(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {

  override def pushData(): Unit = {
    pushHealth()
  }

  private def pushHealth(): Unit = {
    pushMessage(s"health:${Ship.Health.value}:${Ship.Health.max}")
  }
  def pushEvent(eventType: EventType): Unit = {
    pushMessage(s"#event:${eventType}")
  }
}