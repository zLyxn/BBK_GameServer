package org.bbk.gameserver

import java.net.InetSocketAddress
import java.net.InetAddress
import com.sun.net.httpserver.*


class WebServer(connectionEngine: ConnectionEngine):
  private val server = HttpServer.create(new InetSocketAddress(Config.Connection.WEBPORT), 0)
  def start(): Unit =
    server.createContext("/", exchange =>
      val response = s"""
        <!DOCTYPE html>
        <html lang="en">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Dashboard</title>
          <style>
            body { font-family: Arial, sans-serif; text-align: center; }
            .status { font-weight: bold; }
            .online { color: green; }
            .offline { color: red; }
            #commandField { display: none; }
          </style>
          <script>
            function updateGameServerStatus() {
              fetch('/status')
                .then(response => response.text())
                .then(status => {
                  const gameServerStatus = document.getElementById('gameServerStatus');
                  if (status.includes("true")) {
                    gameServerStatus.textContent = "Online";
                    gameServerStatus.className = "status online";
                    commandField.style.display = "block";
                  } else {
                    gameServerStatus.textContent = "Offline";
                    gameServerStatus.className = "status offline";
                    commandField.style.display = "none";
                  }
                })
                .catch(error => {
                  console.error("Error fetching status:", error);
                });
            }

            setInterval(updateGameServerStatus, 5000);
            window.onload = updateGameServerStatus;
          </script>
        </head>
        <body>
          <h1>Welcome to the Dashboard!</h1>
          <p>Game Server: <span id="gameServerStatus" class="status">Checking...</span></p>
          <p>Web Server: <span id="webServerStatus" class="status">Checking...</span></p>
          <p>IP Address: ${InetAddress.getLocalHost.getHostAddress}:${Config.Connection.GAMEPORT}</p>
          <a href="/start">Start the GameServer</a><br>
          <a href="/stop">Stop the GameServer</a><br>
          <a href="/exit">Stop the WebServer</a>
          <div id="commandField">
            <input type="text" id="commandInput" placeholder="Enter value"><br>
            <a id="submit" href="#" onclick="updateLink()">Send command</a>
          </div>
          <script>
            function updateLink() {
              const inputValue = document.getElementById('commandInput').value;
              const baseUrl = '/sendCommand';
              document.getElementById('submit').href = baseUrl + '?' + inputValue;
            }
          </script>
          <footer>
            <a href="/thread">Show all threads</a>
          </footer>
        </body>
        </html>
        """

      sendResponse(exchange, 200, response)
    )
    server.createContext("/start", exchange =>
      val response = "<html><body><h1>You have started the gameserver!</h1><a href=\"/\">Return to dashboard</a></body></html>"
      sendResponse(exchange, 200, response)
      //new Thread(() => connectionEngine.start()).start()
      val gameServerThread = new Thread(() => connectionEngine.start())
      gameServerThread.setName("GameServerThread-connectionEngine")
      gameServerThread.start()
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
    server.createContext("/status", exchange =>
      val response = s"<html><body>${connectionEngine.isRunning}</body></html>" //<a href="/">Return to dashboard</a>
      sendResponse(exchange, 200, response)
    )
    server.createContext("/sendCommand", exchange =>
      val query: String = exchange.getRequestURI.toString
      val command: String = query.split("\\?").last.prepended('#')
      val localClient = new Client(null, connectionEngine.getGameEngine)
      val response = s"<html><body><h1>Command ($command) received:</h1> ${connectionEngine.processCommand(command, localClient).replace("\r\n", "<br>")}</body><a href=\"/\">Return to dashboard</a></html>"
      sendResponse(exchange, 200, response)
      localClient.disconnect()
    )
    server.createContext("/thread", exchange =>
      val response = s"<html><body>${getAllThreads.map(_.getName).mkString("<br>")}</body></html>"
      sendResponse(exchange, 200, response)
    )

    server.setExecutor(null)
    server.start()
    println(s"WebServer is running on http://${InetAddress.getLocalHost.getHostAddress}:${Config.Connection.WEBPORT}/")

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

