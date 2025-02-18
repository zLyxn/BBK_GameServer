package org.bbk.gameserver

import com.sun.tools.attach.VirtualMachine.list

import scala.collection.mutable.ListBuffer

class GameEngine {
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

  class Stat(var value: Int, val max: Int) {
    def setValue(newValue: Int): Unit = {
      value = newValue.max(0).min(max)
    }
  }

  object Health extends Stat(100, 100)
  object ShieldHealth extends Stat(100, 100)
  object Energy extends Stat(100, 100)
  object ShipSpeed extends Stat(100, 100)
  var meteorAmount: Int = 0
  var repairColor: Color = Color.None

  def hitMeteor(meteorColor: Color): Unit = ()

  def gamestart(): Unit = ()
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
}