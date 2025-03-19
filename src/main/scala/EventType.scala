package org.bbk.gameserver

enum EventType {
  case ShieldDownEvent
  case None
}

object EventType {
  //TODO: Remove after testing
  //def fromString(s: String): EventType = s match {
  //  case "ShieldDownEvent" => EventType.ShieldDownEvent
  //}
  
  def fromString(s: String): EventType = {
    val eventTypes = EventType.values
    eventTypes.find(_.toString.equalsIgnoreCase(s)).getOrElse(EventType.None)
  }
  
  
  def fromEvent(event: GameEvent): EventType = {
    EventType.values.find(_.toString == event.getClass.getSimpleName).get
  }
}