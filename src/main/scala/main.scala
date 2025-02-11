package org.bbk.gameserver

@main def main(): Unit = {
  WebServer.start()
  println("Hello World")
  val server = new ConnectionEngine(9999) // Server läuft auf Port 9999
  server.start()
}

