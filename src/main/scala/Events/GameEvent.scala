package org.bbk.gameserver


trait GameEvent {
  var startTime: Int = 0
  var length: Option[Int] = None
  var probability: Option[Float] = None
  def trigger[E <: GameEventCompanion](gameEngine: GameEngine, event: E): Unit = (event.isActive = true)
  //TODO : implement the Actives
  def solve(): Unit = {
    length match {
      case Some(l) if l <= 0 => isActive = false
      case Some(l) => length = Some(l - 1)
      case None => this.finish()
    }
  }
  def finish(): Unit = ()
}
