package org.bbk.gameserver

import com.sun.net.httpserver.*

import scala.compiletime.uninitialized
//import com.sun.net.httpserver.HttpsConfigurator

import java.net.{InetAddress, InetSocketAddress}
import java.security.KeyStore
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import scala.io.Source


class WebServer(connectionEngine: ConnectionEngine):
  //private val server = HttpServer.create(new InetSocketAddress(Config.Connection.WEBPORT), 0)

  private var server: HttpServer = uninitialized
  try {
    server = HttpServer.create(new InetSocketAddress(Config.Connection.WEBPORT), 0)
  } catch {
    case _: java.net.BindException =>
      println(s"Port ${Config.Connection.WEBPORT} is already in use. Trying to find an available port...")
      server = HttpServer.create(new InetSocketAddress(0), 0)
    case e: Exception =>
      println(s"Error starting Webserver on port ${Config.Connection.WEBPORT}: ${e.getMessage}")
      sys.exit(1)
  }

  // Load the keystore
  //private val keystore = KeyStore.getInstance("JKS")
  //keystore.load(getClass.getResourceAsStream("/keystore.jks"), "password".toCharArray)

  // Set up the key manager factory
  //private val kmf = KeyManagerFactory.getInstance("SunX509")
  //kmf.init(keystore, "password".toCharArray)

  // Set up the trust manager factory
  //private val tmf = TrustManagerFactory.getInstance("SunX509")
  //tmf.init(keystore)

  // Set up the SSL context
  //private val sslContext = SSLContext.getInstance("TLS")
  //sslContext.init(kmf.getKeyManagers, tmf.getTrustManagers, null)

  // Set the SSL context for the server
  //server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
  //  override def configure(params: HttpsParameters): Unit = {
  //    val c = sslContext.createSSLEngine.getSSLParameters
  //    params.setSSLParameters(c)
  //  }
  //})


  class ResourceHandler(resourcePath: String):
    def getResponse: String = {
      val source = Source.fromInputStream(getClass.getResourceAsStream(resourcePath))
      try source.mkString
      finally source.close()
    }


  private val indexPage: String = new ResourceHandler("/public/index.html").getResponse
    .replace("$$IP$$", InetAddress.getLocalHost.getHostAddress)
    .replace("$$GAMEPORT$$", connectionEngine.getServerSocket.getLocalPort.toString)
  private val serviceWorkerScript: String = new ResourceHandler("/public/serviceWorker.js").getResponse

  def start(): Unit =
    server.createContext("/", exchange =>
      exchange.getResponseHeaders.set("Permissions-Policy", "geolocation=(self), microphone=()")
      val response = indexPage
      sendResponse(exchange, 200, response)
    )
    server.createContext("/start", exchange =>
      val response = "<html><head><style> body {font-family: Bahnschrift, sans-serif;text-align: center;} .btn{background-color: #8080ff;border: none;color: white;padding: 10px 12px;margin: 4px 2px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;cursor: pointer;border-radius: 100px;width: 15%;} .btn:hover {background-color: #3838ee;} .btn:active{transform: translateY(4px);}</style></head><body><h1>You have started the gameserver!</h1><a href=\"/\" class=\"btn\">Return to dashboard</a></body></html>"
      sendResponse(exchange, 200, response)
      //new Thread(() => connectionEngine.start()).start()
      val gameServerThread = new Thread(() => connectionEngine.start())
      gameServerThread.setName("GameServerThread-connectionEngine")
      gameServerThread.start()
    )
    server.createContext("/stop", exchange =>
      val response = "<html><style> body {font-family: Bahnschrift, sans-serif;text-align: center;} .btn{background-color: #8080ff;border: none;color: white;padding: 10px 12px;margin: 4px 2px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;cursor: pointer;border-radius: 100px;width: 15%;} .btn:hover {background-color: #3838ee;} .btn:active{transform: translateY(4px);}</style></head><body><h1>You have stopped the gameserver!</h1><a href=\"/\" class=\"btn\">Return to dashboard</a></body></html>"
      sendResponse(exchange, 200, response)
      // TODO: When stopping the server causes `java.net.SocketException: Socket closed`
      connectionEngine.stop()
    )
    server.createContext("/exit", exchange =>
      val response = "<html><style> body {font-family: Bahnschrift, sans-serif;text-align: center;} .btn{background-color: #8080ff;border: none;color: white;padding: 10px 12px;margin: 4px 2px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;cursor: pointer;border-radius: 100px;width: 15%;} .btn:hover {background-color: #3838ee;} .btn:active{transform: translateY(4px);}</style></head><body><h1>You have stopped the WebServer!</h1></body></html>"//<a href="/"  class=\"btn\"">Return to dashboard</a>
      sendResponse(exchange, 200, response)
      stop()
    )
    server.createContext("/status", exchange =>
      val response = s"<html><style> body {font-family: Bahnschrift, sans-serif;text-align: center;} .btn{background-color: #8080ff;border: none;color: white;padding: 10px 12px;margin: 4px 2px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;cursor: pointer;border-radius: 100px;width: 15%;} .btn:hover {background-color: #3838ee;} .btn:active{transform: translateY(4px);}</style></head><body>${connectionEngine.isRunning}</body></html>" //<a href="/"  class=\"btn\">Return to dashboard</a>
      sendResponse(exchange, 200, response)
    )
    server.createContext("/sendCommand", exchange =>
      val query: String = exchange.getRequestURI.toString
      val command: String = query.split("\\?").last.prepended('#')
      val localClient = new Client(null, connectionEngine.getGameEngine)
      val response = s"<html><style> body {font-family: Bahnschrift, sans-serif;text-align: center;} .btn{background-color: #8080ff;border: none;color: white;padding: 10px 12px;margin: 4px 2px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;cursor: pointer;border-radius: 100px;width: 15%;} .btn:hover {background-color: #3838ee;} .btn:active{transform: translateY(4px);}</style></head><body><h1>Command ($command) received:</h1> ${connectionEngine.processCommand(command, localClient).replace("\r\n", "<br>")}</body><a href=\"/\" class=\"btn\">Return to dashboard</a></html>"
      sendResponse(exchange, 200, response)
      localClient.disconnect()
    )
    server.createContext("/threads", exchange =>
      val response = s"<html><style> body {font-family: Bahnschrift, sans-serif;text-align: center;} .btn{background-color: #8080ff;border: none;color: white;padding: 10px 12px;margin: 4px 2px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;cursor: pointer;border-radius: 100px;width: 15%;} .btn:hover {background-color: #3838ee;} .btn:active{transform: translateY(4px);}</style></head><body>${getAllThreads.map(_.getName).mkString("<br>")}</body></html>"
      sendResponse(exchange, 200, response)
    )
    server.createContext("/serviceWorker.js", exchange => {
      val response = serviceWorkerScript
      exchange.getResponseHeaders.set("Content-Type", "application/javascript")
      sendResponse(exchange, 200, response)
    })
    server.setExecutor(null)
    server.start()
    println(s"WebServer is running on http${if (server.isInstanceOf[HttpsServer]) "s" else ""}://${InetAddress.getLocalHost.getHostAddress}:${server.getAddress.getPort}/")

  // TODO consider stopping the server when the WebServer is stopping
  def stop(): Unit = server.stop(0)

  //TODO: Move to a separate class; this is a utility method for /thread
  private def getAllThreads: Array[Thread] = {
    val group = Thread.currentThread().getThreadGroup
    val threads = new Array[Thread](group.activeCount())
    group.enumerate(threads)
    threads.filter(_.getName.startsWith("GameServerThread"))
  }

  private def sendResponse(exchange: HttpExchange, statusCode: Int, response: String): Unit = {
    if response.isEmpty then
      exchange.sendResponseHeaders(statusCode, -1)
    else
      exchange.sendResponseHeaders(statusCode, response.getBytes.length)
      val os = exchange.getResponseBody
      os.write(response.getBytes)
      os.close()
  }

