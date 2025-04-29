package org.bbk.gameserver

class WeaponsbrokenEvent extends GameEvent{
  type E = GameEventCompanion[WeaponsbrokenEvent]
  override val companion: E = WeaponsbrokenEvent
  override def finish(): Unit =  WeaponsbrokenEvent.setActiveState(false)
  probability = None
  override def trigger(gameEngine: GameEngine): Unit = {
    super.trigger(gameEngine)
    Ship.weapons = false
    Ship.weaponsWorking = false
    gameEngine.sendCaptainMessage(_.pushWeapons())
  }
  solveCondition = Some(() => Ship.weaponsWorking)
}
object WeaponsbrokenEvent extends GameEventCompanion[WeaponsbrokenEvent] {
  override def create(): WeaponsbrokenEvent = new WeaponsbrokenEvent()
}

  