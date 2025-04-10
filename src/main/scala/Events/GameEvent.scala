package org.bbk.gameserver


trait GameEvent {
  type E <: GameEventCompanion[? <: GameEvent]
  protected val companion: E
  var solveCondition: Option[() => Boolean] = None
  var startTime: Int = 0
  var length: Option[Int] = None
  var probability: Option[Float] = None
  def trigger(gameEngine: GameEngine): Unit = companion.setActiveState(true)
  def isActive: Boolean = companion.getActiveState
  def solve(): Unit = {
    length match {
      case Some(l) if l <= 0 => companion.setActiveState(false)
      case Some(l) => length = Some(l - 1)
      case None => solveCondition match {
        case Some(condition) if condition() => companion.setActiveState(false)
        case _ => ()
      }
    }
  }
  def finish(): Unit = ()
}
