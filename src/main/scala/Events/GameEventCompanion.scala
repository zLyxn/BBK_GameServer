package org.bbk.gameserver


trait GameEventCompanion[T <: GameEvent] {
  private var activeState: Boolean = false

  def getActiveState: Boolean = activeState
  def setActiveState(state: Boolean): Unit = activeState = state
  def create(): T
}