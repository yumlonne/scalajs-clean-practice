package com.github.yumlonne.sjscp.entity

import scala.collection.immutable.ListMap

trait ServerInfo {
  def id: String
  def name: String
  def state: ServerState
  def spec: String
  def extra: Map[String, String]
  def toMap: Map[String, String]
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