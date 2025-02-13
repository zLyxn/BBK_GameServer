package org.bbk.gameserver

@main def main(): Unit = {
  val server = new ConnectionEngine(9999)
  val webServer = new WebServer(server)
  webServer.start()
}

