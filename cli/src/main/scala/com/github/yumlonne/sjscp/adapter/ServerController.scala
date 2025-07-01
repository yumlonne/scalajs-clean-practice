package com.github.yumlonne.sjscp.adapter

import com.github.yumlonne.sjscp.application.gateway.ServerGateway
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import com.github.yumlonne.sjscp.application.usecase.ServerUsecase
import com.github.yumlonne.sjscp.application.presenter.ServerPresenter

class ServerController(val tokens: List[String])(
  using
    serverGateway: ServerGateway,
    serverPresenter: ServerPresenter,
    ec: ExecutionContext,
) {
  def run(): Future[Unit] = {
    tokens match {
      case "list" :: Nil => list()
      case "start" :: id :: Nil => start(id)
      case "stop" :: id :: Nil => stop(id)
      case x => help(x)
    }
  }

  private lazy val serverUsecase = new ServerUsecase()

  private def list(): Future[Unit] = {
    serverUsecase.list()
  }
  private def start(id: String): Future[Unit] = {
    ???
  }
  private def stop(id: String): Future[Unit] = ???

  private def help(x: List[String]): Future[Unit] = {
    // ここでヘルプメッセージを出す
    // TODO: presenter経由にしたいのでpresenterにエラー用のインターフェースをはやす
    ???
  }
}
