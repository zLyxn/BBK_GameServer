package org.bbk.gameserver

@main def main(): Unit = {
  val server = new ConnectionEngine(Config.Connection.GAMEPORT)
  val webServer = new WebServer(server)
  webServer.start()
  sys.addShutdownHook {
    webServer.stop()
    server.stop()
  }
}
