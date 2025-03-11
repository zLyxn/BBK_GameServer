package org.bbk.gameserver

enum Target {
  case Meteor
  case Ship(color: Color)
  case Lootbox
  case None
}