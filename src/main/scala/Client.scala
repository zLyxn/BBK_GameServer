package org.bbk.gameserver

import java.io.IOException
import java.net.Socket

class Client(val socket: Socket) {
  val ip: String = socket.getInetAddress.getHostAddress
  var status: String = "Connecting"
  var role: String = "unknown"

  def disconnect(): Unit = {
    try {
      if (socket != null && !socket.isClosed) {
        socket.shutdownOutput() // Signalisiert dem Server, dass keine Daten mehr gesendet werden
        socket.shutdownInput()  // Signalisiert dem Server, dass keine Daten mehr empfangen werden
        socket.close()          // Schließt die Verbindung
        status = "Disconnected"
      }
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }
}
