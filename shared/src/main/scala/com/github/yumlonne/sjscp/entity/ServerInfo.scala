package com.github.yumlonne.sjscp.entity

case class ServerInfo(
  id: String,
  name: String,
  state: ServerState,
  spec: String,
  extra: Map[String, String],
)

sealed trait ServerState {
  def canStart: Boolean = false
  def canStop: Boolean = false
}
object ServerState {
  case object Running extends ServerState { override def canStop = true }
  case object Stopped extends ServerState { override def canStart = true }
  case class Indifferent(state: String) extends ServerState {
    override def toString(): String = state.toUpperCase()
  }
}