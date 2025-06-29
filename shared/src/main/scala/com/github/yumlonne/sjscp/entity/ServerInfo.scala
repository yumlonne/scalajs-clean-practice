package com.github.yumlonne.sjscp.entity

trait ServerInfo {
  def id: String
  def name: String
  def state: ServerState
  def spec: String
  def extra: Map[String, String]
  def toMap: Map[String, String] = Map(
    "id" -> id,
    "name" -> name,
    "state" -> state.toString,
    "spec" -> spec,
  ) ++ extra.map((k, v) => s"extra:$k" -> v)
}

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