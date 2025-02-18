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

  case class AbstractValue(value: Int, max: Int) //TODO change name

  object Health extends AbstractValue(100, 100)
  object ShieldHealth extends AbstractValue(100, 100)
  object Energy extends AbstractValue(100, 100)
  object ShipSpeed extends AbstractValue(100, 100)
  var meteorAmount: Int = 0
  var repairColor: Color = Color.None
  
  def gamestart(): Unit = 0
  def gamedone(): Unit = 0
  def gameover(): Unit = 0
  def gamewon(): Unit = 0
}
