package org.bbk.gameserver
import com.typesafe.scalalogging.Logger

object Main {
  def main(args: Array[String]): Unit = { // Signatur angepasst, um der JVM zu entsprechen
    Thread.currentThread().setName("GameServerThread-Main") // Thread.currentThread() anstelle von Thread()
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
}