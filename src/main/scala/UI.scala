package org.bbk.gameserver

import java.net.InetSocketAddress
import com.sun.net.httpserver._

class UI

object WebServer:
  def start(): Unit =
    val server = HttpServer.create(new InetSocketAddress(80), 0)
    server.createContext("/", exchange =>
      val response = "<html><body><h1>Welcome to the WebServer!</h1></body></html>"
      exchange.sendResponseHeaders(200, response.getBytes().length)
      val os = exchange.getResponseBody
      os.write(response.getBytes)
      os.close()
    )
    server.setExecutor(null)
    server.start()
    println("WebServer is running on http://localhost:80/")