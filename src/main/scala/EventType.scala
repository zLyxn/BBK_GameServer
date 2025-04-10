package org.bbk.gameserver


//TODO: Automatically generate this enum from the events
enum EventType {
  case ShieldDownEvent
  case AttackEvent
  case WeaponsbrokenEvent
  case DriveBrokenEvent
  case None
}

object EventType {
  def fromString(s: String): EventType = {
    val eventTypes = EventType.values
    eventTypes.find(_.toString.equalsIgnoreCase(s)).getOrElse(EventType.None)
  }
  
  
  def fromEvent(event: GameEvent): EventType = {
    EventType.values.find(_.toString == event.getClass.getSimpleName).get
  }
}