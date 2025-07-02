package com.github.yumlonne.sjscp.application.presenter

import com.github.yumlonne.sjscp.entity.ServerInfo
import com.github.yumlonne.sjscp.entity.ServerActionResult

trait ServerListPresenter {
  // サーバーのリスト表示
  def showServerList(list: List[ServerInfo]): Unit

  // 進行中表示
  def wip(): Unit
  def done(): Unit
}