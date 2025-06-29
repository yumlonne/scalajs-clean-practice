package com.github.yumlonne.sjscp.application.usecase

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import com.github.yumlonne.sjscp.application.presenter.*
import com.github.yumlonne.sjscp.application.gateway.*

class ServerListUsecase()(
  using
    presenter: ServerListPresenter,
    servergateway: ServerGateway,
    ec: ExecutionContext,
  ) {
  
  def run(): Future[Unit] = {
    presenter.wip()
    servergateway.list().map { serverList =>
      presenter.done()
      presenter.present(serverList)
    }
  }
}
