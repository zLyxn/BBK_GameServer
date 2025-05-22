package org.bbk.gameserver

import java.lang.reflect.Field

object Ship {
  case class InitialState(field: String, value: Any)
  var initialStates: List[InitialState] = List.empty

  val health = new Stat(100, 100)
  val energy = new Stat(100, 100)
  @deprecated("Use drive instead")
  val shipSpeed = new Stat(100, 100)
  val coreAir = new Stat(100, 100)
  val drive = new Stat(50, 100)

  var meteorAmount: Int = 0
  var repairColor: Color = Color.None
  var friendlyColor: Color = Color.Blue
  var ammo: Int = 50
  var shield: Boolean = false
  var weapons: Boolean = true
  var airSupply: Boolean = true

  var shieldWorking: Boolean = true
  var weaponsWorking: Boolean = true
  var airSupplyWorking: Boolean = true
  var driveWorking: Boolean = true

  var repairPoints: Int = 0
  val repairPointChance: Stat = new Stat(0, 100)
  
  private def captureInitialState(): Unit = {
    val fields = this.getClass.getDeclaredFields
      .filterNot(_.getName.contains("$"))
      .filterNot(_.getName.contains("InitialState"))
      .filterNot(_.getName.contains("initialStates"))

    initialStates = fields.map { field =>
      field.setAccessible(true)
      val value = field.get(this) match {
        case stat: Stat => stat.value
        case other => other
      }
      InitialState(field.getName, value)
    }.toList
  }
  captureInitialState()

  def reset(): Unit = {

    val fields = this.getClass.getDeclaredFields
      .filterNot(_.getName.contains("$"))
      .filterNot(_.getName.contains("InitialState"))
      .filterNot(_.getName.contains("initialStates"))

    initialStates.foreach(state => {
      val field: Field = fields.find(_.getName == state.field).orNull
      
      if (field != null) {
        field.setAccessible(true)

        field.get(this) match {
          case stat: Stat =>
            stat.setValue(state.value.asInstanceOf[Int])
          case _ =>
            field.set(this, state.value)
        }
      }
    })
  }

  override def toString: String = {
    val fields = this.getClass.getDeclaredFields
      .filterNot(_.getName.contains("$"))
      .filterNot(_.getName == "initialStates")

    fields.map { field =>
      field.setAccessible(true)
      s"${field.getName}: ${field.get(this)}"
    }.mkString("\r\n" + " " * 12).strip()
  }
}