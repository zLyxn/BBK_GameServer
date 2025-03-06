package org.bbk.gameserver

class ShieldDownEvent extends GameEvent{

  val captain: Option[Captain] = None
  
  override def trigger(): Unit = {
    super.trigger()
    if (captain.nonEmpty) then captain.get.pushEvent(EventType.ShieldDownEvent)
  }
  override def finish(): Unit = isActive = false
  probability = Some(0.9f)
  length =  Some(20)
  startTime = 10
}