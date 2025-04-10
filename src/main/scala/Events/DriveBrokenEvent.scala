package org.bbk.gameserver

class DriveBrokenEvent extends GameEvent {
  override def finish(): Unit = isActive = false
  probability = Some(0.9f)
  
  override def trigger(gameEngine: GameEngine): Unit = {
    super.trigger(gameEngine)
    Ship.driveWorking = false
    Ship.drive = false
    gameEngine.findRole(classOf[WeaponsOfficer]).foreach(_.pushNewEnemy())
    gameEngine.findRole(classOf[Pilot]).foreach(_.pushDrive())
  }
}