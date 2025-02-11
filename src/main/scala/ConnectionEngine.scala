package org.bbk.gameserver

import java.net.{ServerSocket, Socket}
import scala.collection.mutable.ListBuffer
import scala.io.BufferedSource

class ConnectionEngine(port: Int) {
  private val serverSocket = new ServerSocket(port)
  private val clients = ListBuffer[Client]()

  def start(): Unit = {
    println(s"Server läuft auf Port $port...")
    while (true) {
      val socket = serverSocket.accept()
      val client = new Client(socket)
      clients += client
      new Thread(() => handleClient(client)).start()
    }
  }

  private def handleClient(client: Client): Unit = {
    val in = new BufferedSource(client.socket.getInputStream).getLines()
    val out = client.socket.getOutputStream

    client.status = "Connected"
    println(s"Neuer Client verbunden: ${client.ip}")

    for (line <- in) {
      val response = processCommand(line)
      out.write((response + "\n").getBytes)
      out.flush()
    }

    client.status = "Disconnected"
    clients -= client
    client.socket.close()
    println(s"Client getrennt: ${client.ip}")
  }

  private def processCommand(command: String): String = {
    command match {
      case "PING" => "PONG"
      case "STATUS" => "Server läuft"
      case _ => "Unbekannter Befehl"
    }
  }
}
