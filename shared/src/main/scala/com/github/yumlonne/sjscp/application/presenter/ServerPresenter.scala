package com.github.yumlonne.sjscp.application.presenter

import com.github.yumlonne.sjscp.entity.ServerInfo
import com.github.yumlonne.sjscp.entity.ServerActionResult

trait ServerPresenter {
  // サーバーのリスト表示
  def showServerList(list: List[ServerInfo]): Unit
  // サーバーに対するアクション結果の表示
  def showServerActionResult(serverActionResult: ServerActionResult): Unit

  // 進行中表示
  def wip(): Unit
  def done(): Unit
}