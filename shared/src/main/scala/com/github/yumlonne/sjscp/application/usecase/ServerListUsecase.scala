package com.github.yumlonne.sjscp.application.usecase

import scala.concurrent.ExecutionContext

import com.github.yumlonne.sjscp.application.presenter.*
import com.github.yumlonne.sjscp.application.gateway.*

class ServerListUsecase()(
  using
    presenter: ServerListPresenter,
    servergateway: ServerGateway,
    ec: ExecutionContext,
  ) {
  
  def run(): Unit = {
    presenter.wip()
    servergateway.list().foreach { serverList =>
      presenter.done()
      presenter.present(serverList)
    }
  }
}
