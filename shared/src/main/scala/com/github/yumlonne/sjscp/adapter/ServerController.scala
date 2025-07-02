package com.github.yumlonne.sjscp.adapter

import com.github.yumlonne.sjscp.application.gateway.ServerGateway
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import com.github.yumlonne.sjscp.application.usecase.ServerListUsecase
import com.github.yumlonne.sjscp.application.presenter.*
import com.github.yumlonne.sjscp.application.usecase.ServerActionUsecase

class ServerController(val tokens: List[String])(
  using
    serverGateway: ServerGateway,
    serverListPresenter: ServerListPresenter,
    serverActionPresenter: ServerActionPresenter,
    ec: ExecutionContext,
) {
  def run(): Future[Unit] = {
    tokens match {
      case "list"  :: Nil       => list()
      case "start" :: id :: Nil => start(id)
      case "stop"  :: id :: Nil => stop(id)
      case x => help(x)
    }
  }

  private lazy val serverUsecase = new ServerListUsecase()
  private def list(): Future[Unit] = serverUsecase.list()

  private lazy val serverActionUsecase = new ServerActionUsecase()
  private def start(id: String): Future[Unit] = serverActionUsecase.start(id)
  private def stop(id: String): Future[Unit] = serverActionUsecase.stop(id)

  private def help(x: List[String]): Future[Unit] = {
    // ここでヘルプメッセージを出す
    ???
  }
}
