package org.bbk.gameserver

class ShieldDownEvent extends GameEvent{

  val captain: Option[Player] = None
  
  override def trigger(): Unit = {
    super.trigger()
    // an Player Captain eine Nachriht senden
    //TODO: captain.getOrElse(new Captain).pushMessage("moin")
  }
  override def finish(): Unit = isActive = false
  probability = Some(0.9f)
  length =  Some(20)
  startTime = 10
}