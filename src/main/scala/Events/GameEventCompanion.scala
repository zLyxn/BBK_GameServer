package org.bbk.gameserver


trait GameEventCompanion[T <: GameEvent] {
  private var activeState: Boolean = false
  
  def isActive: Boolean = activeState
  def setActive(state: Boolean): Unit = activeState = state
  def create(): T
}