package net.javachallenge.util.misc

case class NotImplementedException(message: String) extends Exception(message) {
  def this() = this("")

}