package org.bbk.gameserver

import com.typesafe.scalalogging.Logger

import java.net.{ServerSocket, SocketException}
import scala.collection.mutable.ListBuffer
import scala.compiletime.uninitialized
import scala.io.BufferedSource

class ConnectionEngine(port: Int, logger: Logger) {
  private var serverSocket: ServerSocket = uninitialized
  try {
    serverSocket = new ServerSocket(port)
  } catch {
    case _: java.net.BindException =>
      logger.warn(s"Port $port is already in use. Trying to find an available port...")
      serverSocket = new ServerSocket(0)
    case e: Exception =>
      logger.error(s"Error starting server on port $port: ${e.getMessage}")
      sys.exit(1)
  }
  //private var serverSocket = new ServerSocket(port)
  private val pendingClients = ListBuffer[Client]()
  @volatile private var running = false

  private val gameengine = new GameEngine(logger)
  def getGameEngine: GameEngine = gameengine
  
  def getServerSocket: ServerSocket = serverSocket

  def start(): Unit = {
    logger.info(s"Server is running on port ${serverSocket.getLocalPort}...")
    running = true
    if(serverSocket.isClosed) serverSocket = new ServerSocket(port)
    while (running) {
      try {
        val socket = serverSocket.accept()
        val client = new Client(socket, gameengine)
        pendingClients += client
        val clientThread = new Thread(() => handleClient(client))
        clientThread.setName("GameServerThread-Client-" + client.hashCode())
        clientThread.start()
      } catch {
        case e: SocketException =>
          logger.info("ServerSocket closed, stopping server.")
        case e: Exception =>
          logger.error("Unexpected error:" + e.getMessage)
      }
    }
  }
  
  def isRunning: Boolean = running
  
  def stop(): Unit = {
    running = false
    this.closeAllConections()
    
    serverSocket.close()
    logger.info("Server stopped.")
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
      if !client.silent then logger.info(s"New client connected: ${client.ip} ${client.silent}")
      processClientMessages(client)
    } catch {
      case e: SocketException if client.socket.isClosed =>
        logger.info("client closed, stopping client handler.")
      case e: Exception =>
        logger.error("Unexpected error:" + e.getMessage)
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
        logger.trace(line)
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
    logger.trace(response)
  }

  private def cleanUpClientResources(client: Client): Unit = {
    client.disconnect()
    pendingClients -= client
    gameengine.removeRole(client)
    if !client.silent then logger.info(s"Client disconnected: ${client.ip}")
  }

  def processCommand(command: String, client: Client): String = {
    val parts = command.toLowerCase.stripSuffix("\r\n").split(":") // Remove \r\n and split
    parts.head match {
      case "#ping" => "PONG"
      case "#status" => "Server is running"
      case "#health" if parts.length == 3 => s"health:${parts(1)}/${parts(2)}"
      case "#role" if parts.length == 2 => pendingClients -= client; gameengine.registerRole(client, parts(1));
      case "#start" => gameengine.gamestart(); ""
      case "#debug" => gameengine.debug
      case "#sendBroadcast" if client.socket.getInetAddress.isLoopbackAddress => {
        if (parts.length == 2) gameengine.sendBroadCast(parts(1))
        else if parts.length == 3 then gameengine.sendBroadCast(parts(1), parts(2))
        else gameengine.sendBroadCast("BROADCAST", "TEST-")
      };""
      case _ => gameengine.handleCommands(parts, client)
    }
  }
}