package org.bbk.gameserver

import java.net.Socket

class Captain(socket: Socket) extends Client(socket) {
  def sendHealth(current: Int, max: Int): Unit = {
    sendMessage(s"health:${current}:${max}")
  }
}