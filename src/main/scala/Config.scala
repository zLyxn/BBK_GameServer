package org.bbk.gameserver

object Config {
  type Player = Captain | Engineer | Pilot | WeaponsOfficer
  val WEBPORT = 80
  val GAMEPORT = 9999
}
