package com.github.yumlonne.sjscp.adapter

import com.github.yumlonne.sjscp.entity.ServerInfo
import com.github.yumlonne.sjscp.entity.ServerActionResult
import com.github.yumlonne.sjscp.application.presenter.ServerListPresenter
import com.github.yumlonne.sjscp.adapter.ServerListView

import scala.concurrent.Future
import com.github.yumlonne.sjscp.adapter.ServerListViewModel

class JPServerListPresenter()(
  using
    view: ServerListView,
) extends ServerListPresenter {

  def showServerList(list: List[ServerInfo]): Unit = {
    val headerOpt = Option.when(list.isEmpty)("サーバーがみつかりませんでした")
    val formatted = list.map(this.formatServerInfo)

    val vm = ServerListViewModel(headerMessage = headerOpt, servers = formatted)
    view.show(vm)
  }

  private def formatServerInfo(serverInfo: ServerInfo): List[(String, String)] = {
    serverInfo.toMap.toList
  }

  def showServerActionResult(serverActionResult: ServerActionResult): Unit = {
    view.show(serverActionResult.toString) // 素朴すぎるがまあいいや
  }

  def wip(): Unit = {
    view.showProcessing()
  }

  def done(): Unit = {
    view.doneProcessing()
  }
}
