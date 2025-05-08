package org.bbk.gameserver

enum Target {
  case Meteor
  case Ship(color: Color)
  case Lootbox
  case Enemy
  case None
}

object Target {
  // This is a workaround for the lack of a way to get all values of an enum, when using non-singleton enums.
  val values: Array[Target] = Target.getClass.getDeclaredFields
      .filter(_.getType == classOf[Target])
      .map(_.get(null).asInstanceOf[Target])
      .filter {
        case _: Target.Ship => false
        case _ => true
      }
    
  def toTarget(target: String, color: Color): Target = {
    values.find(_.toString.equalsIgnoreCase(target)).getOrElse(Target.None) match {
      case _: Target.Ship => Target.Ship(color)
      case other => other
    }
  }
}

