package org.bbk.gameserver

import com.typesafe.scalalogging.Logger

import scala.collection.mutable.ListBuffer
import scala.compiletime.ops.any.==
import scala.util.Random

class GameEngine(val logger: Logger) {
  private type Player = Config.Player

  var running: Boolean = true
  private var nthEvent: Int = 0

  def debug: String = {
      s"""
            ${Ship.toString}
            nthEvent: $nthEvent
            EventInterval: ${getEventInterval(nthEvent)}
      """
      /*RESISTANCE: ${Config.Ship.RESISTANCE}
      DAMAGE: ${Config.Ship.DAMAGE}
      ENERGY_GAIN: ${Config.Ship.ENERGY_GAIN}*/
  }
  
  
  def gamestart(): Unit = {
    gameLoop()
    sendBroadCast("game:start")
  }
  
  def gamedone(): Unit = ()
  def gamewon(): Unit = ()

  def gameover(reason: String): Unit = {
    logger.info("Game Over: " + reason)
    sendBroadCast(s"game:over: $reason")

    // TODO: Ein richtiges gameover
    //  mit beenden des spiels
    //  und der Option ein neues Spiel zu starten
  }
  
  private var playerList: ListBuffer[Player] = ListBuffer[Player]()

  def registerRole(client: Client, role: String): String = {
    if (playerList.size >= 4) {
      return "error:All roles are assigned"
    }

    if (isAssignedRole(role)) {
      return s"error:Role $role is already assigned"
    }

    val player: Option[Player] = role match {
      case "Captain" => Some(new Captain(client.socket, this))
      case "Engineer" => Some(new Engineer(client.socket, this))
      case "Pilot" => Some(new Pilot(client.socket, this))
      case "WeaponsOfficer" => Some(new WeaponsOfficer(client.socket, this))
      case _ =>
        logger.warn(s"Unknown role: $role")
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
    if !client.silent then logger.info(s"Player removed: ${client.ip}")
    client
  }

  def clearPlayers(): ListBuffer[Client] = {
    playerList.clone().map(removeRole)
  }

  private def getEventInterval(nthEvent: Int): Int = {
    ((Config.Game.MAXIMUMINTERVAL-Config.Game.MINIMUMINTERVALL)/(1+ Math.pow(Math.E,(Config.Game.SLOPE*(nthEvent-Config.Game.HORIZONTALDISPLACEMENT))))+Config.Game.MINIMUMINTERVALL).toInt
  }

  def handleCommands(parts: Array[String], client: Client): String = {
    val player = playerList.find(_.socket == client.socket)
    player match {
      case Some(p) => handlePlayerCommands(parts, p)
      case None => {
        client.handleClientCommands(parts)
      }
    }
  }

  private def handlePlayerCommands(parts: Array[String], player: Player): String = {
    val commandResult = player.handleCommands(parts)
    if (commandResult.isEmpty) {
      s"error:Unknown command:${parts.head}"
    } else {
      commandResult.get
    }
  }

  private def gameLoop(): Unit = {
    eventLoop()
    dataLoop()
    tickLoop()
  }

  private def startEvent(): Unit = {
    val activeEventType: EventType = Events.startRandomEvent(this)
    if(activeEventType != EventType.None) {
      sendCaptainMessage(_.pushEvent(activeEventType))
    }
  }

  def findRole[T](role: Class[T]): ListBuffer[T] = {
    playerList.filter(_.getClass == role).asInstanceOf[ListBuffer[T]]
  }
  private def eventLoop(): Unit = {
    val eventLoop = new Thread(new Runnable {
      var count: Int = 0

      override def run(): Unit = {
        while (running) {
          count += 1
          Thread.sleep(1000)
          Events.solveEvents()
          val eventInterval = getEventInterval(nthEvent)
          if count >= eventInterval then {
            nthEvent += 1
            logger.debug(s"Event triggert: $nthEvent after $count seconds")
            count = 0
            startEvent()
          }
        }
      }
    })
    eventLoop.setName("GameServerThread-EventLoop")
    eventLoop.start()
  }
  
  private def dataLoop(): Unit = {
    val dataLoop = new Thread(new Runnable {
      override def run(): Unit = {
        while (running) {
          Thread.sleep(Config.Game.DATAUPDATEINTERVAL)
          playerList.foreach(_.pushData())
        }
      }
    })
    dataLoop.setName("GameServerThread-DataLoop")
    dataLoop.start()
  }

  private def tickLoop(): Unit = {
    val tickLoop = new Thread(new Runnable {
      override def run(): Unit = {
        while (running) {
          Thread.sleep(Config.Game.TICKINTERVAL)
          checkCoreAir()
          createRepairPoint()
        }
      }
    })
    tickLoop.setName("GameServerThread-TickLoop")
    tickLoop.start()
  }

  private def checkCoreAir(): Unit = {
    if(!Ship.airSupply){
      if (Ship.coreAir.value <= 0) {
        gameover(Config.Game.Deathmessages.SUFFOCATED)
      }else{
        reduceCoreAir()
      }
    }
  }

  private def reduceCoreAir(): Unit = {
    if (Random.nextInt(100) < Config.Game.COREAIRLOSSCHANCE) {
      Ship.coreAir -= 1
      sendCaptainMessage(_.pushCoreAir())
      //TODO: vielleicht zu viel Traffic
    }
  }
  
  private def createRepairPoint(): Unit = {
    if (Random.nextInt(100) < Ship.repairPointChance.value) {
      Ship.repairPoints += 1
      sendCaptainMessage(_.pushRepairPoints())
    }
    Ship.repairPointChance -= Config.Game.REPAIRPOINTCHANCELOSS
  }

  def sendCaptainMessage(action: Captain => Unit): Unit = {
    findRole(classOf[Captain]).foreach(action)
  }
  private def sendBroadCast(text: String): Unit = {
    playerList.foreach(_.pushMessage(text))
  }

  def decapitalize(str: String): String = {
    if (str.isEmpty) str
    else str.head.toLower + str.tail
  }
}