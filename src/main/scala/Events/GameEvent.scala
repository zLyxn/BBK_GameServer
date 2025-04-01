package org.bbk.gameserver


trait GameEvent {
  var isActive: Boolean = false
  var startTime: Int = 0
  var length: Option[Int] = None
  var probability: Option[Float] = None
  def trigger(): Unit = (isActive = true)
  def solve(): Unit = {
    length match {
      case Some(l) if l <= 0 => isActive = false
      case Some(l) => length = Some(l - 1)
      case None => this.finish()
    }
  }
  def finish(): Unit = ()
}
