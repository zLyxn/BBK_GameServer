package org.bbk.gameserver

import scala.compiletime.uninitialized


trait GameEvent {
  type E <: GameEventCompanion[? <: GameEvent]
  protected val companion: E
  var startTime: Int = 0
  var length: Option[Int] = None
  var probability: Option[Float] = None
  def trigger(gameEngine: GameEngine): Unit = companion.setActiveState(true)
  def isActive: Boolean = companion.getActiveState
  def solve(): Unit = {
    length match {
      case Some(l) if l <= 0 => companion.setActiveState(false)
      case Some(l) => length = Some(l - 1)
      case None => ()
    }
  }
  def finish(): Unit = ()
}
