package org.bbk.gameserver

import java.net.{ServerSocket, SocketException}
import scala.collection.mutable.ListBuffer
import scala.io.BufferedSource

class ConnectionEngine(port: Int) {
  private var serverSocket = new ServerSocket(port)
  private val pendingClients = ListBuffer[Client]()
  @volatile private var running = false

  private val gameengine = new GameEngine

  def start(): Unit = {
    println(s"Server is running on port $port...")
    running = true
    if(serverSocket.isClosed) serverSocket = new ServerSocket(port)
    while (running) {
      try {
        val socket = serverSocket.accept()
        val client = new Client(socket)
        pendingClients += client
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
  
  private def closeAllConections(): Unit ={
    pendingClients.foreach(_.disconnect())
    pendingClients.clear()
    //gameengine.removeRole()
    
    // playerlist -> foreach: client von player disconnecten
  } 
  // TODO: Nutzen um Player disconecten

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
    pendingClients -= client
    gameengine.removeRole(client)
    // TODO: Client returnen zum disconecten
    println(s"Client disconnected: ${client.ip}")
  }

  private def processCommand(command: String, client: Client): String = {
    val parts = command.stripSuffix("\r\n").split(":") // Remove \r\n and split
    parts.head match {
      case "#ping" => "PONG"
      case "#status" => "Server is running"
      case "#health" if parts.length == 3 => s"health:${parts(1)}/${parts(2)}"
      case "#role" if parts.length == 2 => gameengine.registerRole(client, parts(1)); pendingClients -= client; s"Role set to ${parts(1)}"
      case "#hitmeteor" if parts.length == 2 => gameengine.hitMeteor(gameengine.Color.toColor(parts(1))); ""
      case "#start" => gameengine.gamestart(); ""
      case "#debug" => gameengine.debug
      case _ => "error:Unknown command"
    }
  }
}