package org.bbk.gameserver

class WeaponsbrokenEvent extends GameEvent{
  override def finish(): Unit =  ShieldDownEvent.setActive(false)
  probability = Some(0.9f)
  override def trigger(gameEngine: GameEngine): Unit = {
    Ship.weapons = false
    Ship.weaponsWorking = false
    gameEngine.sendCaptainMessage(_.pushWeapons())
  }
}
object WeaponsbrokenEvent extends GameEventCompanion[WeaponsbrokenEvent] {
  override def create(): WeaponsbrokenEvent = new WeaponsbrokenEvent()
}

  