package org.bbk.gameserver

import scala.collection.mutable.ListBuffer
import scala.compiletime.ops.any.==

class GameEngine {
  type Player = Config.Player

  var running: Boolean = true
  private var nthEvent: Int = 0

  def debug: String = {
    s"""
      Health: ${Ship.Health}
      Energy: ${Ship.Energy}
      ShipSpeed: ${Ship.ShipSpeed}
      Shield: ${Ship.Shield}
      meteorAmount: ${Ship.meteorAmount}
      repairColor: ${Ship.repairColor}
      nthEvent: ${nthEvent}
      EventInterval: ${getEventInterval(nthEvent)}
    """
    /*RESISTANCE: ${Config.Ship.RESISTANCE}
    DAMAGE: ${Config.Ship.DAMAGE}
    ENERGY_GAIN: ${Config.Ship.ENERGY_GAIN}*/
  }

  // TODO: zu viele Vals ohne Caps
  

  def gamestart(): Unit = {
    gameLoop()
  }
  def gamedone(): Unit = ()
  def gameover(): Unit = ()
  def gamewon(): Unit = ()
  
  private var playerList: ListBuffer[Player] = ListBuffer[Player]()

  def registerRole(client: Client, role: String): String = {
    if (playerList.size >= 4) {
      return "error:All roles are assigned"
    }

    if (isAssignedRole(role)) {
      return s"error:Role $role is already assigned"
    }

    val player: Option[Player] = role match {
      case "Captain" => Some(new Captain(client.socket))
      case "Engineer" => Some(new Engineer(client.socket))
      case "Pilot" => Some(new Pilot(client.socket))
      case "WeaponsOfficer" => Some(new WeaponsOfficer(client.socket))
      case _ =>
        println(s"Unknown role: $role")
        return s"error:Unknown role $role"
    }

    player.foreach(savePlayer)
    s"Role set to $role"
  }

  private def isAssignedRole(role: String): Boolean = {
    role match {
      case "Captain" => playerList.exists(_.isInstanceOf[Captain])
      case "Engineer" => playerList.exists(_.isInstanceOf[Engineer])
      case "Pilot" => playerList.exists(_.isInstanceOf[Pilot])
      case "WeaponsOfficer" => playerList.exists(_.isInstanceOf[WeaponsOfficer])
      case _ => false
    }
  }

  private def savePlayer(client: Player): Unit = {
    playerList += client
  }

  def removeRole(client: Client): Client = {
    playerList = playerList.filterNot(_.socket == client.socket)
    println(s"Player removed: ${client.ip}")
    client
  }

  def clearPlayers(): ListBuffer[Client] = {
    playerList.clone().map(removeRole)
  }

  def getEventInterval(nthEvent: Int): Int = {
    ((Config.Game.MAXIMUMINTERVAL-Config.Game.MINIMUMINTERVALL)/(1+ Math.pow(Math.E,(Config.Game.SLOPE*(nthEvent-Config.Game.HORIZONTALDISPLACEMENT))))+Config.Game.MINIMUMINTERVALL).toInt
  }

  def handleCommands(parts: Array[String], client: Client): String = {
    val player = playerList.find(_.socket == client.socket)
    player match {
      case Some(p) => handlePlayerCommands(parts, p)
      case None => s"Player not found for client: ${client.ip}"
    }
  }

  def handlePlayerCommands(parts: Array[String], player: Player): String = {
    val commandResult = player.handleCommands(parts)
    if (commandResult.isEmpty) {
      s"error:Unknown command:${parts.head}"
    } else {
      commandResult.get
    }
  }

  def gameLoop(): Unit = {
    eventLoop()
    dataLoop()
  }

  private def eventLoop(): Unit = {
    val eventLoop = new Thread(new Runnable {
      var count: Int = 0

      override def run(): Unit = {
        while (running) {
          count += 1
          Thread.sleep(1000)
          var eventInterval = getEventInterval(nthEvent)
          if count >= eventInterval then {
            nthEvent += 1
            println(s"Event triggert: ${nthEvent} after ${count} seconds") // Debug
            count = 0
          }
        }
      }
    })
    eventLoop.start()
  }

  // TODO: EVENTuell zu einem Event machen, um SPAM zu verhindern
  private def dataLoop(): Unit = {
    val dataLoop = new Thread(new Runnable {
      override def run(): Unit = {
        while (running) {
          Thread.sleep(3000)
          playerList.foreach(_.pushData())
        }
      }
    })
    dataLoop.start()
  }
}