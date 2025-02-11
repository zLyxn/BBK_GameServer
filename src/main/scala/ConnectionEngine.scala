package org.bbk.gameserver

import java.net.{ServerSocket, Socket}
import scala.collection.mutable.ListBuffer
import scala.io.BufferedSource

class ConnectionEngine(port: Int) {
  private val serverSocket = new ServerSocket(port)
  private val clients = ListBuffer[Client]()
  @volatile private var running = true

  def start(): Unit = {
    println(s"Server läuft auf Port $port...")
    while (running) {
      val socket = serverSocket.accept()
      val client = new Client(socket)
      clients += client
      new Thread(() => handleClient(client)).start()
    }
  }

  def stop(): Unit = {
    running = false
    serverSocket.close()
    println("Server gestoppt.")
  }

  private def handleClient(client: Client): Unit = {
    val input = new BufferedSource(client.socket.getInputStream).getLines()
    val output = client.socket.getOutputStream

    client.status = "Connected"
    println(s"Neuer Client verbunden: ${client.ip}")

    for (line <- input) {
      if (line.startsWith("#")) { // Nur Befehle mit # verarbeiten
        val response = processCommand(line)
        output.write((response + "\r\n").getBytes)
        output.flush()
      } else {
        output.write("#error:Ungültiges Format\r\n".getBytes)
        output.flush()
      }
    }

    client.status = "Disconnected"
    clients -= client
    client.socket.close()
    println(s"Client getrennt: ${client.ip}")
  }

  private def processCommand(command: String): String = {
    val parts = command.stripSuffix("\r\n").split(":") // Entfernt \r\n und splittet
    parts.head match {
      case "#ping" => "PONG"
      case "#status" => "Server läuft"
      case "#health" if parts.length == 3 => s"Health:${parts(1)}/${parts(2)}"
      case _ => "#error:Unbekannter Befehl"
    }
  }
}
