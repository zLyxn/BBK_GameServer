package org.bbk.gameserver

class Stat(var value: Int, val max: Int) {
  def setValue(newValue: Int): Unit = {
    value = newValue.max(0).min(max)
  }
  def +=(amount: Int): Unit = {
    setValue(value + amount)
  }
  def -=(amount: Int): Unit = {
    setValue(value - amount)
  }
  override def toString: String = s"Value: $value - Max: $max" // + "-" * 5 + super.toString
}
