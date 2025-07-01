package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.application.presenter.ServerPresenter
import com.github.yumlonne.sjscp.entity.ServerInfo
import scala.concurrent.Future
import com.github.yumlonne.sjscp.entity.ServerActionResult

class ServerConsolePresenter()(
  using
    view: ConsoleView,
) extends ServerPresenter {

  def showServerList(list: List[ServerInfo]): Unit = {
    val formatted = list.map(this.formatServerInfo)
    val msg = formatted.mkString("\n")
    view.show(msg)
  }

  private def formatServerInfo(serverInfo: ServerInfo): String = {
    serverInfo.toMap.toList.sortBy(_._1).map { (k, v) =>
      s"\t$k: $v"
    }.mkString("Server:\n", "\n", "\n")
  }

  def showServerActionResult(serverActionResult: ServerActionResult): Unit = {
    view.show(serverActionResult.toString) // 素朴すぎるがまあいいや
  }

  def wip(): Unit = {
    view.show("now processing...")
  }

  def done(): Unit = {
    // コンソールの場合終わったことは必要な情報の出力によって明示されるため何もしない
  }
}
