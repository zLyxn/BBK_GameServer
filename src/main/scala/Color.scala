package org.bbk.gameserver

enum Color {
  case Red
  case Blue
  case Green
  case Yellow
  //case Purple
  //case Orange
  //case Black
  //case White
  //case Grey
  case None

  def asString: String = this.toString
}

object Color {
  def fromString(color: String): Color = {
    val colors = Color.values
    colors.find(_.toString.equalsIgnoreCase(color)).getOrElse(Color.None)
  }
}