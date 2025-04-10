package org.bbk.gameserver

object Events {
  private val events: List[GameEvent] = List(ShieldDownEvent(), AttackEvent(), WeaponsbrokenEvent(), DriveBrokenEvent()) // TODO: Hier alle Events automatisch hinzufügen

  def getActiveEvents: List[GameEvent] = events.filter(_.isActive)

  private def getInactiveEvents: List[GameEvent] = events.filterNot(_.isActive)


  def startRandomEvent(gameEngine: GameEngine): EventType = {
    //println(s"BEFORE!! Active: ${getActiveEvents.map(_.getClass.getSimpleName)}              Inactive: ${getInactiveEvents.map(_.getClass.getSimpleName)}")
    solveEvents() //TODO move this to the game engine
    val inactiveEvents = getInactiveEvents
    if (inactiveEvents.nonEmpty) {
      val event = inactiveEvents(scala.util.Random.nextInt(inactiveEvents.length))
      event.trigger(gameEngine)
      //println(s"AFTER:: Active: ${getActiveEvents.map(_.getClass.getSimpleName)}              Inactive: ${getInactiveEvents.map(_.getClass.getSimpleName)}")
      return EventType.fromEvent(event)
    }
    EventType.None
  }
  def solveEvents(): Unit = {
    getActiveEvents.foreach(_.solve())
  }
}