package org.bbk.gameserver

import java.net.Socket

class Captain(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {
  def pushHealth(current: Int, max: Int): Unit = {
    pushMessage(s"health:${current}:${max}")
  }
  def pushEvent(eventType: EventType): Unit = {

    pushMessage("Moin")
  }
}