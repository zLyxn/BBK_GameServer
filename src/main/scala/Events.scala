package org.bbk.gameserver

import com.typesafe.scalalogging.Logger
import scala.reflect.runtime.universe

object Events {
  private val events: List[GameEvent] = List(ShieldDownEvent(), AttackEvent(), WeaponsbrokenEvent(), DriveBrokenEvent()) // TODO: Hier alle Events automatisch hinzufügen

  val mirror = universe.runtimeMirror(getClass.getClassLoader)
  val eventClasses = mirror.staticModule("org.bbk.gameserver.Events").info.decls
    .collect {
      case symbol if symbol.isClass && symbol.name.toString.endsWith("Event") =>
        mirror.reflectClass(symbol.asClass).reflectConstructor(symbol.asClass.primaryConstructor.asMethod)()
    }
    .toList
  assert(events == eventClasses, "Wrong Events loaded! Please check the events list.")

  var logger: Option[Logger] = None

  if logger.isDefined then logger.get.debug("Events loaded: " + events.map(_.getClass.getSimpleName).mkString(", "))
  println("Debug: Events loaded: " + events.map(_.getClass.getSimpleName).mkString(", "))

  def getActiveEvents: List[GameEvent] = events.filter(_.isActive)

  private def getInactiveEvents: List[GameEvent] = events.filterNot(_.isActive)


  def startRandomEvent(gameEngine: GameEngine): EventType = {
    solveEvents()
    val inactiveEvents = getInactiveEvents
    if (inactiveEvents.nonEmpty) {
      val eventProbabilities: List[(GameEvent, Float)] = inactiveEvents.map(e => e -> e.probability.getOrElse(1.0f / inactiveEvents.size))
      val totalProbability: Float = eventProbabilities.map(_._2).sum
      val rand: Float = scala.util.Random.nextFloat() * totalProbability
      var cumulative: Float = 0.0f
      val event: Option[GameEvent] = eventProbabilities.find { case (_, prob) =>
        cumulative += prob
        rand < cumulative
      }.map(_._1)
      if (event.isEmpty) {
        return EventType.None
      }
      event.get.trigger(gameEngine)
      return EventType.fromEvent(event.get)
    }
    EventType.None
  }
  def solveEvents(): Unit = {
    getActiveEvents.foreach(_.solve())
  }
}