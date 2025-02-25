package org.bbk.gameserver

class ShieldDownEvent extends GameEvent{
  override def trigger(): Unit = super.trigger()
  override def solve(): Unit = super.solve()
  length =  Some(20)
  startTime = 10
}