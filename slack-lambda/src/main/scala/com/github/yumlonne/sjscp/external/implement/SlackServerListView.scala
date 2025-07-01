package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.adapter.ServerListViewModel
import com.github.yumlonne.sjscp.adapter.ServerListView

import scala.concurrent.Future

// TODO impl
class SlackServerListView extends ServerListView {
  def show(s: String): Future[Unit] = {
    println(s)
    Future.unit
  }
  def show(servers: ServerListViewModel): Future[Unit] = {
    println(servers.toString())
    Future.unit
  }
}
