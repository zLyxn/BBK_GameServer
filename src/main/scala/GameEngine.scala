package org.bbk.gameserver

class GameEngine {
  enum Color {
    case Red
    case Blue
    case Green
    case Yellow
    case Purple
    case Orange
    case Black
    case White
    case Grey
    case None

    def asString: String = this.toString
  }

  class Stat(var value: Int, val max: Int) {
    def setValue(newValue: Int): Unit = {
      value = newValue.max(0).min(max)
    }
  }

  object Health extends Stat(100, 100)
  object ShieldHealth extends Stat(100, 100)
  object Energy extends Stat(100, 100)
  object ShipSpeed extends Stat(100, 100)
  var meteorAmount: Int = 0
  var repairColor: Color = Color.None

  def hitMeteor(meteorColor: Color): Unit = 0

  def gamestart(): Unit = 0
  def gamedone(): Unit = 0
  def gameover(): Unit = 0
  def gamewon(): Unit = 0
}
