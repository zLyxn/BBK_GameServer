package org.bbk.gameserver

trait GameEvent {
  var startTime: Int = 0
  var length: Option[Int] = None
  def trigger(): Unit = ()
  def solve(): Unit = ()
}
