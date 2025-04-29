package org.bbk.gameserver

class AttackEvent extends GameEvent{
  type E = GameEventCompanion[AttackEvent]
  override val companion: E = AttackEvent
  override def finish(): Unit =  AttackEvent.setActiveState(false)
  probability = None
  override def trigger(gameEngine: GameEngine): Unit = {
    super.trigger(gameEngine)
    gameEngine.findRole(classOf[Pilot]).foreach(_.pushNewEnemy())
    gameEngine.findRole(classOf[WeaponsOfficer]).foreach(_.pushNewEnemy())
  }
  // TODO : Add a condition to check if the ship is under attack
}
object AttackEvent extends GameEventCompanion[AttackEvent] {
  override def create(): AttackEvent = new AttackEvent()
}