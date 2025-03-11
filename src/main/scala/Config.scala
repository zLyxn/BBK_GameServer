package org.bbk.gameserver

object Config {
  type Player = Captain | Engineer | Pilot | WeaponsOfficer
  val WEBPORT = 80
  val GAMEPORT = 9999
  
  object Ship {
    val RESISTANCE: Float = 0.5
    val DAMAGE: Int = 10
    val ENERGY_GAIN: Int = 5
  }
}
