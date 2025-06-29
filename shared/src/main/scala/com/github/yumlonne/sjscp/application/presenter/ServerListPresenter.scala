package com.github.yumlonne.sjscp.application.presenter

import com.github.yumlonne.sjscp.entity.ServerInfo

trait ServerListPresenter {
  def present(list: List[ServerInfo]): Unit
}