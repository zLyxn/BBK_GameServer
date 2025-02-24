package org.bbk.gameserver

trait GameEvent {
  val startTime: Int = 0
  val length: Option[Int] = None
  def trigger(): Unit = ()
  def solve(): Unit = ()
}
