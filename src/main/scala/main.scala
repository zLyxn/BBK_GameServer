package org.bbk.gameserver
import com.typesafe.scalalogging.Logger

@main def main(): Unit = {
  Thread().setName("GameServerThread-Main")
  val logger: Logger = Logger("GameServerLogger")
  Events.logger = Some(logger)
  val server = new ConnectionEngine(Config.Connection.GAMEPORT, logger)
  val webServer = new WebServer(server, logger)
  webServer.start()
  val _ = sys.addShutdownHook {
    webServer.stop()
    server.stop()
  }
}
