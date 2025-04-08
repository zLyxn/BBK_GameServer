package org.bbk.gameserver

class AttackEvent extends GameEvent{
  override def finish(): Unit = isActive = false
  probability = Some(0.9f)
  override def trigger(gameEngine: GameEngine): Unit = {
    super.trigger(gameEngine)
    gameEngine.findRole(classOf[Pilot]).foreach(_.pushNewEnemy())
    gameEngine.findRole(classOf[WeaponsOfficer]).foreach(_.pushNewEnemy())
    gameEngine.findRole(classOf[Captain]).foreach(_.pushEvent(EventType.AttackEvent))
  }
}