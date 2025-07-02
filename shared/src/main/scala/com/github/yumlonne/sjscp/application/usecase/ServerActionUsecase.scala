package com.github.yumlonne.sjscp.application.usecase

import com.github.yumlonne.sjscp.entity.*
import com.github.yumlonne.sjscp.application.presenter.ServerActionPresenter
import com.github.yumlonne.sjscp.application.gateway.ServerGateway

import scala.concurrent.{Future, ExecutionContext}

class ServerActionUsecase()(
  using
    presenter: ServerActionPresenter,
    servergateway: ServerGateway,
    ec: ExecutionContext,
  ) {
  
  def start(id: String): Future[Unit] = {
    val resultFuture = action(id) { s => servergateway.start(s.id) } { _.state.canStart }
    resultFuture.map { result =>
      presenter.showServerActionResult(result)
    }
  }

  def stop(id: String): Future[Unit] = {
    val resultFuture = action(id) { s => servergateway.stop(s.id) } { _.state.canStop }
    resultFuture.map{ result =>
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
