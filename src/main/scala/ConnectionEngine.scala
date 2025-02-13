package org.bbk.gameserver

import java.net.{ServerSocket, Socket}
import scala.collection.mutable.ListBuffer
import scala.io.BufferedSource

class ConnectionEngine(port: Int) {
  private val serverSocket = new ServerSocket(port)
  private val clients = ListBuffer[Client]()
  @volatile private var running = false

  def start(): Unit = {
    println(s"Server is running on port $port...")
    running = true
    while (running) {
      val socket = serverSocket.accept()
      val client = new Client(socket)
      clients += client
      new Thread(() => handleClient(client)).start()
    }
  }
  
  def isRunning() = running
  
  def stop(): Unit = {
    running = false
    serverSocket.close()
    println("Server stopped.")
  }

  private def handleClient(client: Client): Unit = {
    val input = new BufferedSource(client.socket.getInputStream).getLines()
    val output = client.socket.getOutputStream

    client.status = "Connected"
    println(s"New client connected: ${client.ip}")

    for (line <- input) {
      println(line)
      if (line.startsWith("#")) { // Only process commands starting with #
        val response = processCommand(line)
        output.write((response + "\r\n").getBytes)
        output.flush()
        println(s"${response}")
      } else {
        output.write("error:Invalid format\r\n".getBytes)
        output.flush()
        println("error:Invalid format")
      }
    }

    client.status = "Disconnected"
    clients -= client
    client.socket.close()
    println(s"Client disconnected: ${client.ip}")
  }

  private def processCommand(command: String): String = {
    val parts = command.stripSuffix("\r\n").split(":") // Remove \r\n and split
    parts.head match {
      case "#ping" => "PONG"
      case "#status" => "Server is running"
      case "#health" if parts.length == 3 => s"Health:${parts(1)}/${parts(2)}"
      case _ => "error:Unknown command"
    }
  }
}
