package org.bbk.gameserver

import java.net.InetSocketAddress
import com.sun.net.httpserver.*

import javax.swing.text.html.HTML

class WebServer(connectionEngine: ConnectionEngine):
  private val server = HttpServer.create(new InetSocketAddress(80), 0)
  def start(): Unit =
    server.createContext("/", exchange =>
      val response = """
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
            </style>
            <script>
              async function checkServerStatus(url, elementId) {
                try {
                  const response = await fetch(url, { method: 'HEAD' });
                  if (response.ok) {
                    document.getElementById(elementId).innerText = "Online";
                    document.getElementById(elementId).className = "status online";
                  } else {
                    throw new Error();
                  }
                } catch (error) {
                  document.getElementById(elementId).innerText = "Offline";
                  document.getElementById(elementId).className = "status offline";
                }
              }

              function updateStatus() {
                //checkServerStatus("http://localhost:80/status", "gameServerStatus");
                //checkServerStatus("http://localhost:80", "webServerStatus");
                console.log("Updates are disabled for now.");
              }

              //setInterval(updateStatus, 5000); // Update every 5 seconds
              window.onload = updateStatus;
            </script>
          </head>
          <body>
            <h1>Welcome to the Dashboard!</h1>
            <p>Game Server: <span id="gameServerStatus" class="status">Checking...</span></p>
            <p>Web Server: <span id="webServerStatus" class="status">Checking...</span></p>
            <a href="/start">Start the GameServer</a><br>
            <a href="/stop">Stop the GameServer</a><br>
            <a href="/exit">Stop the WebServer</a>
          </body>
          </html>
        """

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
    server.createContext("/status", exchange =>
      val response = s"<html><body>${connectionEngine.isRunning()}</body></html>" //<a href="/">Return to dashboard</a>
      sendResponse(exchange, 200, response)
    )

    server.setExecutor(null)
    server.start()
    println("WebServer is running on http://localhost:80/")

  // TODO consider stopping the server when the WebServer is stopping
  def stop(): Unit = server.stop(0)

  private def sendResponse(exchange: HttpExchange, statusCode: Int, response: String): Unit =
    if response.isEmpty then
      exchange.sendResponseHeaders(statusCode, -1)
    else
      exchange.sendResponseHeaders(statusCode, response.getBytes.length)
      val os = exchange.getResponseBody
      os.write(response.getBytes)
      os.close()
