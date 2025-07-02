package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.adapter.ServerListView
import com.github.yumlonne.sjscp.adapter.ServerListViewModel

import scala.concurrent.Future

class ConsoleServerListView extends ServerListView {
  def show(vm: ServerListViewModel): Unit = {
    println()
    vm.headerMessage.foreach(println)

    val serversStr = vm.servers
      .map { server =>
        server.map { (k, v) =>
          s"\t$k: $v"
        }.mkString("Server:\n", "\n", "\n")
      }.mkString("\n")

    println(serversStr)
  }

  def show(s: String): Unit = {
    println(s)
  }

  def showProcessing(): Unit = {
    println("processing...")
  }
  def doneProcessing(): Unit = {
  }
}
