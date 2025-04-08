package org.bbk.gameserver

class ShieldDownEvent extends GameEvent{
  
  override def finish(): Unit = isActive = false
  probability = Some(0.9f)
  length =  Some(20)
  startTime = 10
}
//TODO: overide def trigger