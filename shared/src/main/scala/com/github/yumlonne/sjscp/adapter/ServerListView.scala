package com.github.yumlonne.sjscp.adapter

import scala.concurrent.Future

trait ServerListView extends SimpleView {
  def show(vm: ServerListViewModel): Future[Unit]
  def showProcessing(): Future[Unit]
  def doneProcessing(): Future[Unit]
}
