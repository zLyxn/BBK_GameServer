package org.bbk.gameserver

import java.net.Socket
import scala.util.Random

class Pilot(socket: Socket, gameEngine: GameEngine) extends Client(socket, gameEngine) {
  override def handleRoleCommands(parts: Array[String]): String = {
    parts.head match {
      case "#hitmeteor" if parts.length == 2 => hitMeteor(Color.fromString(parts(1))); ""
      case _ => super.handleRoleCommands(parts)
    }
  }
  override def pushData(): Unit = {
    pushShipSpeed()
    pushMeteorAmount()
  }


  private def hitMeteor(meteorColor: Color): Unit = {
    println(s"Hit meteor: ${meteorColor}:${Ship.repairColor == meteorColor}")
    if (Ship.repairColor == meteorColor) {
      Ship.energy += Config.Ship.ENERGY_GAIN
      Ship.repairPointChance += Config.Game.REPAIRPOINTCHANCEGAIN
    } else {
      Ship.health -= (Config.Ship.DAMAGE * (1 - (if Ship.shield then Config.Ship.RESISTANCE else 0))).toInt
      randomSystemDown()
    }
  }
  
  def pushShipSpeed(): Unit = {
    if(Ship.drive && Ship.driveWorking){
      pushMessage(s"#shipSpeed:${Ship.shipSpeed.value}:${Ship.shipSpeed.max}")
    }
    pushMessage(s"#shipSpeed:0:${Ship.shipSpeed.max}")
  }
  private def pushMeteorAmount(): Unit = {
    pushMessage(s"#meteorAmount:${Ship.meteorAmount}")
  }

  private def randomSystemDown(): Unit = {
    if (Random.nextInt(100) < Config.Game.SYSTEMDOWNCHANCE) {
      println("one System down")
      val system = randomSystem()

      println(s"System: ${system}")

      //TODO: vals in Config
      // java.lang.NoSuchFieldException: Shield
      //        at java.base/java.lang.Class.getDeclaredField(Class.java:2707)
      //        at org.bbk.gameserver.Pilot.randomSystemDown$$anonfun$1(Pilot.scala:48)
      //        at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
      //        at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
      //        at scala.collection.ArrayOps$.foreach$extension(ArrayOps.scala:1323)
      //        at org.bbk.gameserver.Pilot.randomSystemDown(Pilot.scala:47)
      //        at org.bbk.gameserver.Pilot.hitMeteor(Pilot.scala:26)
      //        at org.bbk.gameserver.Pilot.handleRoleCommands(Pilot.scala:9)
      //        at org.bbk.gameserver.Client.handleCommands(Client.scala:38)
      //        at org.bbk.gameserver.GameEngine.handlePlayerCommands(GameEngine.scala:107)
      //        at org.bbk.gameserver.GameEngine.handleCommands(GameEngine.scala:101)
      //        at org.bbk.gameserver.ConnectionEngine.processCommand(ConnectionEngine.scala:113)
      //        at org.bbk.gameserver.ConnectionEngine.processClientMessages$$anonfun$1(ConnectionEngine.scala:78)
      //        at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
      //        at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
      //        at scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
      //        at scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
      //        at scala.collection.AbstractIterator.foreach(Iterator.scala:1303)
      //        at org.bbk.gameserver.ConnectionEngine.processClientMessages(ConnectionEngine.scala:75)
      //        at org.bbk.gameserver.ConnectionEngine.handleClient(ConnectionEngine.scala:59)
      //        at org.bbk.gameserver.ConnectionEngine.$anonfun$1(ConnectionEngine.scala:24)
      //        at java.base/java.lang.Thread.run(Thread.java:1623)

      System.values.foreach { system =>
        val field = Ship.getClass.getDeclaredField(system.toString)
        field.setAccessible(true)
        field.setBoolean(Ship, false)

        val workingField = Ship.getClass.getDeclaredField(system.toString + "Working")
        workingField.setAccessible(true)
        workingField.setBoolean(Ship, false)
      }

      gameEngine.findRole(classOf[Captain]).foreach { captain =>
        captain.getClass.getDeclaredMethod("push" + system.toString).invoke(captain)
      }
    }
  }

  def callPushFunction(functionName: String): Unit = {
    val method = this.getClass.getDeclaredMethod(functionName)
    method.invoke(this)
  }

  private def randomSystem(): System = {
    System.values(Random.nextInt(System.values.length))
  }
}