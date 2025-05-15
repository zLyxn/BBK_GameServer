package org.bbk.gameserver

import scala.compiletime.uninitialized

object Ship {
  private case class InitialState(field: String, value: Any)
  private var initialStates: List[InitialState] = uninitialized
  
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

  // Speichert den Initialzustand beim ersten Laden der Klasse
  captureInitialState()

  private def captureInitialState(): Unit = {
    val fields = this.getClass.getDeclaredFields
      .filterNot(_.getName.contains("$"))
      .filterNot(_.getName == "initialStates")
    
    initialStates = fields.map { field =>
      field.setAccessible(true)
      val value = field.get(this) match {
        case stat: Stat => stat.value // Für Stat-Objekte speichern wir nur den Wert
        case other => other // Für alle anderen Typen speichern wir den direkten Wert
      }
      InitialState(field.getName, value)
    }.toList
  }

  def reset(): Unit = {
    initialStates.foreach { state =>
      val field = this.getClass.getDeclaredField(state.field)
      field.setAccessible(true)
      
      field.get(this) match {
        case stat: Stat => 
          // Für Stat-Objekte setzen wir den Wert über die setValue-Methode
          stat.setValue(state.value.asInstanceOf[Int])
        case _ => 
          // Für alle anderen Felder setzen wir den Wert direkt
          field.set(this, state.value)
      }
    }
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