package com.github.yumlonne.sjscp.application.usecase

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import com.github.yumlonne.sjscp.application.presenter.*
import com.github.yumlonne.sjscp.application.gateway.*
import com.github.yumlonne.sjscp.entity.ServerActionResult
import com.github.yumlonne.sjscp.entity.ServerInfo

class ServerUsecase()(
  using
    presenter: ServerPresenter,
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

  def start(id: String): Future[Unit] = {
    presenter.wip()
    val resultFuture = action(id) { s => servergateway.start(s.id) } { _.state.canStart }
    resultFuture.map { result =>
      presenter.done()
      presenter.showServerActionResult(result)
    }
  }

  def stop(id: String): Future[Unit] = {
    presenter.wip()
    val resultFuture = action(id) { s => servergateway.stop(s.id) } { _.state.canStop }
    resultFuture.map{ result =>
      presenter.done()
      presenter.showServerActionResult(result)
    }
  }

  private def action(id: String)
    (act: ServerInfo => Future[ServerActionResult])
    (stateTest: ServerInfo => Boolean)
  : Future[ServerActionResult] = {
    servergateway.list().flatMap { serverList =>
      val serverOpt = serverList.find(_.id == id)
      val validation: Either[ServerActionResult, ServerInfo] =
        serverOpt.toRight(ServerActionResult.ServerNotFound)
          .flatMap { server =>
            Either.cond(
              stateTest(server),
              server,
              ServerActionResult.UnexpectedState(server.state)
            )
          }

      validation match {
        case Left(error) => Future.successful(error)
        case Right(server) => act(server)
      }
    }
  }
}
