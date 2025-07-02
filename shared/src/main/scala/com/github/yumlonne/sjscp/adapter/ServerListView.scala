package com.github.yumlonne.sjscp.adapter

import scala.concurrent.Future

trait ServerListView extends SimpleView {
  def show(vm: ServerListViewModel): Unit
  def showProcessing(): Unit
  def doneProcessing(): Unit
}
