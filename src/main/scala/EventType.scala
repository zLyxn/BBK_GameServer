package org.bbk.gameserver

enum EventType {
  case ShieldDownEvent
  case AttackEvent
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