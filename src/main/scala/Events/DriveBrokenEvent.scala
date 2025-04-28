package org.bbk.gameserver

class DriveBrokenEvent extends GameEvent {
  type E = GameEventCompanion[DriveBrokenEvent]
  override val companion: E = DriveBrokenEvent
  override def finish(): Unit = DriveBrokenEvent.setActiveState(false)
  probability = Some(0.9f)
  solveCondition = Some(() => Ship.driveWorking)
  override def trigger(gameEngine: GameEngine): Unit = {
    super.trigger(gameEngine)
    Ship.driveWorking = false
    Ship.drive.setValue(0)
    gameEngine.findRole(classOf[WeaponsOfficer]).foreach(_.pushNewEnemy())
    gameEngine.findRole(classOf[Pilot]).foreach(_.pushDrive())
  }
}
object DriveBrokenEvent extends GameEventCompanion[DriveBrokenEvent] {
  override def create(): DriveBrokenEvent = new DriveBrokenEvent()
}