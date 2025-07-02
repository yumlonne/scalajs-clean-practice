package com.github.yumlonne.sjscp.application.usecase

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import com.github.yumlonne.sjscp.application.presenter.*
import com.github.yumlonne.sjscp.application.gateway.*
import com.github.yumlonne.sjscp.entity.ServerActionResult
import com.github.yumlonne.sjscp.entity.ServerInfo

class ServerListUsecase()(
  using
    presenter: ServerListPresenter,
    servergateway: ServerGateway,
    ec: ExecutionContext,
  ) {
  
  def list(): Future[Unit] = {
    presenter.wip()
    servergateway.list().map { serverList =>
      presenter.done()
      presenter.showServerList(serverList)
    }
  }
}
