package com.github.yumlonne.sjscp.application.presenter

import com.github.yumlonne.sjscp.entity.ServerInfo

trait ServerListPresenter {
  // メインの結果表示
  def present(list: List[ServerInfo]): Unit

  // 進行中表示
  def wip(): Unit
  def done(): Unit
}