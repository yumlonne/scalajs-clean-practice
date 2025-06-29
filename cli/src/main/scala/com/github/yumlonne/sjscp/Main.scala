package com.github.yumlonne.sjscp

import com.github.yumlonne.sjscp.entity.*

@main def main() = {
  println("hello!")
  val s = ServerState.Running
  println(s)
  println(s.canStop)
}