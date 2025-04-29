package org.bbk.gameserver

import com.typesafe.scalalogging.Logger

object Events {
  //private val events_old: List[GameEvent] = List(ShieldDownEvent(), AttackEvent(), WeaponsbrokenEvent(), DriveBrokenEvent()) // TODO: Hier alle Events automatisch hinzufügen

  var logger: Option[Logger] = None

  private val events: List[GameEvent] = {
    val packageName = "org.bbk.gameserver.events"
    val excludedClasses = Set("GameEvent", "GameEventCompanion") // Falls bestimmte Klassen ausgeschlossen werden sollen
    val runtimeMirror = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)
    val packageSymbol = runtimeMirror.staticPackage(packageName)

    packageSymbol.info.decls
      .filter(_.isClass)
      .map(_.asClass)
      .filterNot(cls => excludedClasses.contains(cls.name.toString))
      .filter(cls => cls.name.toString.endsWith("Event"))
      .flatMap { cls =>
        try {
          val clazz = runtimeMirror.runtimeClass(cls)
          if (classOf[GameEvent].isAssignableFrom(clazz)) {
            Some(clazz.getDeclaredConstructor().newInstance().asInstanceOf[GameEvent])
          } else None
        } catch {
          case e: Exception =>
            if logger.isDefined then logger.get.error("Error loading event class: " + cls.name + ": " + e.getMessage)
            println("Error loading event class: " + cls.name + ": " + e.getMessage)
            None
        }
      }
      .toList
  }
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