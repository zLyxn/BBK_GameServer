package org.bbk.gameserver

class ShieldDownEvent extends GameEvent{
  type E = GameEventCompanion[ShieldDownEvent]
  override val companion: E = ShieldDownEvent
  override def finish(): Unit = ShieldDownEvent.setActiveState(false)
  probability = None
  length =  Some(20)
  startTime = 10


  override def trigger(gameEngine: GameEngine): Unit = {
    super.trigger(gameEngine)
    Ship.shield = false
    Ship.shieldWorking = false
    gameEngine.findRole(classOf[Captain]).foreach(_.pushShield())
  }
}
object ShieldDownEvent extends GameEventCompanion[ShieldDownEvent] {
  override def create(): ShieldDownEvent = new ShieldDownEvent()
}