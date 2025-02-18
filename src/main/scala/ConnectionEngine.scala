package org.bbk.gameserver

import java.net.{ServerSocket, SocketException}
import scala.collection.mutable.ListBuffer
import scala.io.BufferedSource

class ConnectionEngine(port: Int) {
  private var serverSocket = new ServerSocket(port)
  private val clients = ListBuffer[Client]()
  type Player = Captain | Engineer | Pilot | WeaponsOfficer
  var playerList = ListBuffer[Player]()
  @volatile private var running = false

  def start(): Unit = {
    println(s"Server is running on port $port...")
    running = true
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
    try {
      client.status = "Connected"
      println(s"New client connected: ${client.ip}")
      processClientMessages(client)
    } catch {
      case e: SocketException if client.socket.isClosed =>
        println("client closed, stopping client handler.")
      case e: Exception =>
        e.printStackTrace()
    } finally {
      cleanUpClientResources(client)
    }
  }

  private def processClientMessages(client: Client): Unit = {
    val source = new BufferedSource(client.socket.getInputStream)
    val input = source.getLines()
    val output = client.socket.getOutputStream
    try {
      for (line <- input) {
        println(line)
        if (line.startsWith("#")) { // Only process commands starting with #
          val response = processCommand(line, client)
          sendResponse(output, response)
        } else {
          sendResponse(output, "error:Invalid format")
        }
      }
    } finally {
      source.close()
      output.close()
    }
  }

  private def sendResponse(output: java.io.OutputStream, response: String): Unit = {
    output.write((response + "\r\n").getBytes)
    output.flush()
    println(response)
  }

  private def cleanUpClientResources(client: Client): Unit = {
    client.disconnect()
    clients -= client
    removePlayer(client)
    println(s"Client disconnected: ${client.ip}")
  }

  private def processCommand(command: String, client: Client): String = {
    val parts = command.stripSuffix("\r\n").split(":") // Remove \r\n and split
    parts.head match {
      case "#ping" => "PONG"
      case "#status" => "Server is running"
      case "#health" if parts.length == 3 => s"health:${parts(1)}/${parts(2)}"
      case "#role" if parts.length == 2 => registerRole(client, parts(1)); s"Role set to ${parts(1)}"
      case _ => "error:Unknown command"
    }
  }

  private def registerRole(client: Client, role: String): Unit = {

    val players: Option[Player] = role match {
      case "Captain" => Some(new Captain(client.socket))
      case "Engineer" => Some(new Engineer(client.socket))
      case "Pilot" => Some(new Pilot(client.socket))
      case "WeaponsOfficer" => Some(new WeaponsOfficer(client.socket))
      case _ =>
        println(s"Unknown role: $role")
        None
    }
    players.foreach(savePlayer)
  }

  private def savePlayer(client: Player): Unit = {
    playerList += client
  }

  private def removePlayer(client: Client): Unit = {
    playerList = playerList.filterNot {
      case player: Captain => player.socket == client.socket
      case player: Engineer => player.socket == client.socket
      case player: Pilot => player.socket == client.socket
      case player: WeaponsOfficer => player.socket == client.socket
    }
    println(s"Player removed: ${client.ip}")
  }
}