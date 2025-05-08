package org.bbk.gameserver

import com.typesafe.scalalogging.Logger
import io.github.classgraph.ClassGraph
import scala.jdk.CollectionConverters._

object Events {
  private var events: Option[List[GameEvent]] = None
  
  def initEvents(): Unit = {
    if (events.isDefined) {
      logger.get.warn("Events already initialized")
      return
    }
    val scanResult = new ClassGraph()
      .enableClassInfo()
      .acceptPackages("org.bbk.gameserver")
      .scan()

    this.events = Some(scanResult
      .getClassesImplementing("org.bbk.gameserver.GameEvent")
      .asScala
      .toList
      .map(classInfo => Class.forName(classInfo.getName).getDeclaredConstructor().newInstance().asInstanceOf[GameEvent])
    )
  }
  
  var logger: Option[Logger] = None

  if logger.isDefined then logger.get.debug("Events loaded: " + events.map(_.getClass.getSimpleName).mkString(", "))
  println("Debug: Events loaded: " + events.map(_.getClass.getSimpleName).mkString(", "))

  def getActiveEvents: List[GameEvent] = events.get.filter(_.isActive)

  private def getInactiveEvents: List[GameEvent] = events.get.filterNot(_.isActive)


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