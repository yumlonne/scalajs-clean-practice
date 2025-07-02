package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.adapter.ServerListViewModel
import com.github.yumlonne.sjscp.adapter.ServerListView
import com.github.yumlonne.sjscp.external.slack.SlackClient

import scala.concurrent.Future
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

// TODO: Viewの表示自体はFutureではなく同期的にし、resultだけ同期的にしたい
class SlackServerListView()(
  using
    slackClient: SlackClient
) extends ServerListView {

  private val futures: mutable.ListBuffer[Future[Unit]] = new ListBuffer()
  def show(s: String): Future[Unit] = {
    futures.addOne(slackClient.post(s))
    Future.unit
  }
// TODO slack block kit impl
  def show(servers: ServerListViewModel): Future[Unit] = {
    futures.addOne(slackClient.post(servers.toString))
    Future.unit
  }

  def showProcessing(): Future[Unit] = {
    futures.addOne(slackClient.reaction("eyes"))
    Future.unit
  }

  // XXX: リアクションを取り消す?
  def doneProcessing(): Future[Unit] = {
    Future.unit
  }

  def result()(using ec: ExecutionContext): Future[Unit] = {
    Future.sequence(futures.toList).map(_ => ())
  }
}
