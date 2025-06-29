package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.application.presenter.ServerListPresenter
import com.github.yumlonne.sjscp.entity.ServerInfo
import scala.concurrent.Future

class ServerListConsolePresenter()(
  using
    view: ConsoleView,
) extends ServerListPresenter {
  def present(list: List[ServerInfo]): Unit = {
    val formatted = list.map(this.format)
    val msg = formatted.mkString("\n")
    view.show(msg)
  }

  private def format(serverInfo: ServerInfo): String = {
    serverInfo.toMap.toList.sortBy(_._1).map { (k, v) =>
      s"\t$k: $v"
    }.mkString("Server:\n", "\n", "\n")
  }
}
