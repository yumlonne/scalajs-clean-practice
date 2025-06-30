package com.github.yumlonne.sjscp

import scalajs.js
import scala.scalajs.js.annotation.*

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

@JSExportTopLevel(name = "handler", moduleID = "index")
def main(event: js.Object, context: js.Object): Future[Unit] = {
  given ExecutionContext = ExecutionContext.global

  println("hello lambda from Scala.js!")
  Future(())
}