package org.bbk.gameserver

object Config {
  type Player = Captain | Engineer | Pilot | WeaponsOfficer
  object Connection {
    val WEBPORT = 80
    val GAMEPORT = 9999
    val SILENCE_LOOPBACK = true
  }
  object Game {
    val FRIENDLYKILLALLOWED: Int = 3
    val DATAUPDATEINTERVAL: Int = 6000
    val MAXIMUMINTERVAL: Int = 180 //180
    val SLOPE: Float = 0.7
    val HORIZONTALDISPLACEMENT: Int = 5
    val MINIMUMINTERVALL: Int = 30
    val TICKINTERVAL = 100
    val COREAIRLOSSCHANCE: Int = 25
    val REPAIRPOINTCHANCELOSS: Int = 2
    val REPAIRPOINTCHANCEGAIN: Int = 1
    val SYSTEMDOWNCHANCE: Int = 100
    object Deathmessages {
      val FRIENDLYFIRE: String = "3 Friendly Ships killed"
      val SUFFOCATED: String = "Suffocated"
    }
  }
  object Ship {
    val RESISTANCE: Float = 0.5
    val DAMAGE: Int = 10
    val ENERGY_GAIN: Int = 5
    val AMMO_GAIN: Int = 3
  }
}
