package com.github.yumlonne.sjscp.entity

sealed trait ServerActionResult

object ServerActionResult {
  case object Success extends ServerActionResult
  case object AlreadyInExpectedState extends ServerActionResult
  case object ServerNotFound extends ServerActionResult
  case class UnexpectedState(state: ServerState) extends ServerActionResult
  case class UnexpectedError(message: String) extends ServerActionResult
}