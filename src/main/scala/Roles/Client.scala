package org.bbk.gameserver

import java.io.IOException
import java.net.Socket

class Client(var socket: Socket, gameEngine: GameEngine) {
  var silent = false
  if (socket == null) socket = new java.net.Socket("localhost", Config.Connection.GAMEPORT)
  val ip: String = socket.getInetAddress.getHostAddress
  var status: String = "Connecting"

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
        gameEngine.logger.error("Unexpected error:" + e.getMessage)
    }
  }
  
  def pushMessage(message: String): Unit = {
    val output = this.socket.getOutputStream
    output.write((message + "\r\n").getBytes)
    output.flush()
  }
  def pushData(): Unit = ()
  def pushStart(): Unit =  pushMessage("#game:start")
  def pushLoss(): Unit =  pushMessage("#game:over")
  def pushWin(): Unit =  pushMessage("#game:won")

  def handleCommands(parts: Array[String]): Option[String] = {
    Some(parts.head match {
      case "#game" => "Game triggert"
      case _ => handleRoleCommands(parts)
    })
  }

  def handleRoleCommands(parts: Array[String]): String = s"error:Unknown command:${parts.head}"
}
