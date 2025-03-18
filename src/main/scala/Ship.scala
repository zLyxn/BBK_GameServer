package org.bbk.gameserver

object Ship {
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
    override def toString: String = s"Value: ${value} - Max: ${max}" // + "-" * 5 + super.toString
  }
  
  object Health extends Stat(100, 100)
  object Energy extends Stat(100, 100)
  object ShipSpeed extends Stat(100, 100)
  object CoreAir extends Stat(100, 100) // TODO: send Captain + debug

  var Shield: Boolean = false
  var meteorAmount: Int = 0
  var repairColor: Color = Color.None
  var friendlyColor: Color = Color.Blue
  var ammo: Int = 50
  var weapons: Boolean = true
  var airSupply: Boolean = true // TODO: send Captain + debug

  override def toString: String = {
    val members = this.getClass.getDeclaredFields.map { field =>
      field.setAccessible(true)
      s"${field.getName}: ${field.get(this)}"
    }
    members.mkString("\n")
  }
}
