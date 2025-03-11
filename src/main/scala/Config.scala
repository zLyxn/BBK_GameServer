package org.bbk.gameserver

object Config {
  type Player = Captain | Engineer | Pilot | WeaponsOfficer
  object Connection {
    val WEBPORT = 80
    val GAMEPORT = 9999
  }
  object Game {
    val MAXIMUMINTERVAL: Int = 180
    val SLOPE: Float = 0.7
    val HORIZONTALDISPLACEMENT: Int = 5
    val MINIMUMINTERVALL: Int = 30
  }
  object Ship {
    val RESISTANCE: Float = 0.5
    val DAMAGE: Int = 10
    val ENERGY_GAIN: Int = 5
  }
}
