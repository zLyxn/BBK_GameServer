package org.bbk.gameserver

import java.net.Socket

class Client(val socket: Socket) {
  val ip: String = socket.getInetAddress.getHostAddress
  var status: String = "Connecting"
}
