package org.bbk.gameserver

enum EventType {
  case ShieldDownEvent
  case None
}

object EventType {
  def fromString(s: String): EventType = s match {
    case "ShieldDownEvent" => EventType.ShieldDownEvent
  }
  def fromEvent(event: GameEvent): EventType = {
    EventType.values.find(_.toString == event.getClass.getSimpleName).get
  }
}