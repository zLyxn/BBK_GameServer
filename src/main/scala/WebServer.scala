package org.bbk.gameserver

import java.net.InetSocketAddress
import com.sun.net.httpserver._

class WebServer(connectionEngine: ConnectionEngine):
  private val server = HttpServer.create(new InetSocketAddress(80), 0)
  def start(): Unit =
    server.createContext("/", exchange =>
      val response = "<html><body><h1>Welcome to the Dashboard!</h1><a href=\"/start\">Start the WebServer</a><br><a href=\"/stop\">Stop the GameServer</a><br><a href=\"/exit\">Stop the WebServer</a></body></html>"
      sendResponse(exchange, 200, response)
    )
    server.createContext("/start", exchange =>
      val response = "<html><body><h1>You have started the gameserver!</h1><a href=\"/\">Return to dashboard</a></body></html>"
      sendResponse(exchange, 200, response)
      new Thread(() => connectionEngine.start()).start()
    )
    server.createContext("/stop", exchange =>
      val response = "<html><body><h1>You have stopped the gameserver!</h1><a href=\"/\">Return to dashboard</a></body></html>"
      sendResponse(exchange, 200, response)
      // TODO: When stopping the server causes `java.net.SocketException: Socket closed`
      connectionEngine.stop()
    )
    server.createContext("/exit", exchange =>
      val response = "<html><body><h1>You have stopped the WebServer!</h1></body></html>"//<a href="/">Return to dashboard</a>
      sendResponse(exchange, 200, response)
      stop()
    )

    server.setExecutor(null)
    server.start()
    println("WebServer is running on http://localhost:80/")

  // TODO consider stopping the server when the WebServer is stopping
  def stop(): Unit = server.stop(0)

  private def sendResponse(exchange: HttpExchange, statusCode: Int, response: String): Unit =
    exchange.sendResponseHeaders(statusCode, response.getBytes.length)
    val os = exchange.getResponseBody
    os.write(response.getBytes)
    os.close()
