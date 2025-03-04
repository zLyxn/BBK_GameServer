package org.bbk.gameserver

import scala.collection.mutable.ListBuffer

class GameEngine {

  var running: Boolean = true
  private var nthEvent: Int = 0
  private val maximumInterval: Int = 180
  private val slope: Float = 0.7
  private val horizontalDisplacement: Int = 5
  private val minimumInterval: Int = 30

  def debug: String = {
    s"""
      Health: ${Health}
      Energy: ${Energy}
      ShipSpeed: ${ShipSpeed}
      Shield: ${Shield}
      meteorAmount: ${meteorAmount}
      repairColor: ${repairColor}
      RESISTANCE: ${RESISTANCE}
      DAMAGE: ${DAMAGE}
      nthEvent: ${nthEvent}
      EventInterval: ${getEventInterval(nthEvent)}
    """
  }
  enum Color {
    case Red
    case Blue
    case Green
    case Yellow
    case Purple
    case Orange
    case Black
    case White
    case Grey
    case None

    def asString: String = this.toString
  }

  object Color {
    def toColor(color: String): Color = {
      val colors = Color.values
      colors.find(_.toString.equalsIgnoreCase(color)).getOrElse(Color.None)
    }
  }

  class Stat(var value: Int, val max: Int) {
    def setValue(newValue: Int): Unit = {
      value = newValue.max(0).min(max)
    }
    def -=(amount: Int): Unit = {
      setValue(value - amount)
    }

    override def toString: String = s"Value: ${value} - Max: ${max}"// + "-" * 5 + super.toString
  }

  object Health extends Stat(100, 100)
  object Energy extends Stat(100, 100)
  object ShipSpeed extends Stat(100, 100)
  var Shield: Boolean = false
  var meteorAmount: Int = 0
  var repairColor: Color = Color.None
  val RESISTANCE: Float = 0.5
  val DAMAGE: Int = 10

  // TODO: zu viele Vals ohne Caps

  def hitMeteor(meteorColor: Color): Unit = {
    if(repairColor == meteorColor){
      // positiv: + neue Energie
    }else{
      Health -= (DAMAGE*(1 - (if Shield then RESISTANCE else 0))).toInt
    }
  }

  def gamestart(): Unit = {
    gameLoop()
  }
  def gamedone(): Unit = ()
  def gameover(): Unit = ()
  def gamewon(): Unit = ()

  type Player = Captain | Engineer | Pilot | WeaponsOfficer
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