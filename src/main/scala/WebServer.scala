package org.bbk.gameserver

import java.net.InetSocketAddress
import com.sun.net.httpserver._

object WebServer:
  private val server = HttpServer.create(new InetSocketAddress(80), 0)
  def start(): Unit =
    server.createContext("/", exchange =>
      val response = "<html><body><h1>Welcome to the Dashboard!</h1><a href=\"/stop\">Stop the GameServer</a></body></html>"
      sendResponse(exchange, 200, response)
    )

    server.createContext("/stop", exchange =>
      val response = "<html><body><h1>You have stopped the gameserver!</h1><a href=\"/\">Return to dashboard</a></body></html>"
      sendResponse(exchange, 200, response)
      println("STOP THE CONNECTION")
    )

    server.setExecutor(null)
    server.start()
    println("WebServer is running on http://localhost:80/")
  def stop(): Unit = server.stop(0)

  private def sendResponse(exchange: HttpExchange, statusCode: Int, response: String): Unit =
    exchange.sendResponseHeaders(statusCode, response.getBytes.length)
    val os = exchange.getResponseBody
    os.write(response.getBytes)
    os.close()
