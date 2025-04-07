package org.bbk.gameserver
import com.typesafe.scalalogging.Logger

@main def main(): Unit = {
  val logger: Logger = Logger("GameServerLogger")
  val server = new ConnectionEngine(Config.Connection.GAMEPORT, logger)
  val webServer = new WebServer(server, logger)
  webServer.start()
  sys.addShutdownHook {
    webServer.stop()
    server.stop()
  }
}
