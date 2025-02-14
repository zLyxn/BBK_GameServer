package org.bbk.gameserver

import java.io.PrintWriter
import java.net.{ServerSocket, Socket, SocketException}
import scala.collection.mutable.ListBuffer
import scala.io.BufferedSource

class ConnectionEngine(port: Int) {
  private var serverSocket = new ServerSocket(port)
  private val clients = ListBuffer[Client]()
  @volatile private var running = false

  def start(): Unit = {
    println(s"Server is running on port $port...")
    running = true
    // Hier kann ServerSocket nicht accepted werden und somit kein neuer Client verbinden
    if(serverSocket.isClosed) serverSocket = new ServerSocket(port)
    while (running) {
      try {
        val socket = serverSocket.accept()
        val client = new Client(socket)
        clients += client
        new Thread(() => handleClient(client)).start()
      } catch {
        case e: SocketException =>
          println("ServerSocket closed, stopping server.")
        case e: Exception =>
          e.printStackTrace()
      }
    }
  }
  
  def isRunning: Boolean = running
  
  def stop(): Unit = {
    running = false
    this.closeAllConections()
    
    serverSocket.close()
    println("Server stopped.")
  }
  
  private def closeAllConections(): Unit = for (client <- clients) client.disconnect()

  private def handleClient(client: Client): Unit = {
    val source = new BufferedSource(client.socket.getInputStream)
    val input = source.getLines()
    val output = client.socket.getOutputStream

    client.status = "Connected"
    println(s"New client connected: ${client.ip}")
    try {
    for (line <- input) {
      println(line)
      if (line.startsWith("#")) { // Only process commands starting with #
        val response = processCommand(line, client)
        output.write((response + "\r\n").getBytes)
        output.flush()
        println(s"${response}")
      } else {
        output.write("error:Invalid format\r\n".getBytes)
        output.flush()
        println("error:Invalid format")
      }
    }
    } catch {
      case e: SocketException if client.socket.isClosed =>
        println("client closed, stopping client handler.")
      case e: Exception =>
        e.printStackTrace()
    } finally {
      try {
        source.close()
        output.close()
        client.disconnect()
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }

    clients -= client
    println(s"Client disconnected: ${client.ip}")
  }

  private def processCommand(command: String, client: Client): String = {
    val parts = command.stripSuffix("\r\n").split(":") // Remove \r\n and split
    parts.head match {
      case "#ping" => "PONG"
      case "#status" => "Server is running"
      case "#health" if parts.length == 3 => s"Health:${parts(1)}/${parts(2)}"
      case "#role" if parts.length == 2 => registerRole(client, parts(1)); s"Role set to ${parts(1)}"
      case _ => "error:Unknown command"
    }
  }

  private def registerRole(client: Client, role: String): Unit = client.role = role
}
