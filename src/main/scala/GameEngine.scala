package org.bbk.gameserver

import scala.collection.mutable.ListBuffer
import scala.compiletime.ops.any.==

class GameEngine {
  type Player = Config.Player

  var running: Boolean = true
  private var nthEvent: Int = 0
  private val maximumInterval: Int = 180
  private val slope: Float = 0.7
  private val horizontalDisplacement: Int = 5
  private val minimumInterval: Int = 30

  def debug: String = {
    s"""
      Health: ${Ship.Health}
      Energy: ${Ship.Energy}
      ShipSpeed: ${Ship.ShipSpeed}
      Shield: ${Ship.Shield}
      meteorAmount: ${Ship.meteorAmount}
      repairColor: ${Ship.repairColor}
      RESISTANCE: ${Ship.RESISTANCE}
      DAMAGE: ${Ship.DAMAGE}
      nthEvent: ${nthEvent}
      EventInterval: ${getEventInterval(nthEvent)}
    """
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
    ((maximumInterval-minimumInterval)/(1+ Math.pow(Math.E,(slope*(nthEvent-horizontalDisplacement))))+minimumInterval).toInt
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
    val thread = new Thread(new Runnable {
      var count: Int = 0

      override def run(): Unit = {
        while (running) {
          count += 1
          Thread.sleep(1000)
          var eventInterval = getEventInterval(nthEvent)
          if count >= eventInterval then {
            nthEvent += 1
            println(s"Event triggert: ${nthEvent} after ${count} seconds")
            count = 0
          }
        }
      }
    })
    thread.start()
  }
}