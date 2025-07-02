package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.adapter.ServerListViewModel
import com.github.yumlonne.sjscp.adapter.ServerListView
import com.github.yumlonne.sjscp.adapter.FutureView
import com.github.yumlonne.sjscp.external.slack.SlackClient

import scala.concurrent.Future
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

// TODO: Viewの表示自体はFutureではなく同期的にし、resultだけ同期的にしたい
class SlackServerListView()(
  using
    slackClient: SlackClient
) extends ServerListView with FutureView {

  private val futures: mutable.ListBuffer[Future[Unit]] = new ListBuffer()
  def show(s: String): Unit = {
    futures.addOne(slackClient.post(s))
    Future.unit
  }
// TODO slack block kit impl
  def show(servers: ServerListViewModel): Unit = {
    futures.addOne(slackClient.post(servers.toString))
    Future.unit
  }

  def showProcessing(): Unit = {
    futures.addOne(slackClient.reaction("eyes"))
    Future.unit
  }

  // XXX: リアクションを取り消す?
  def doneProcessing(): Unit = {
    Future.unit
  }

  def completed()(using ec: ExecutionContext): Future[Unit] = {
    Future.sequence(futures.toList).map(_ => ())
  }
}
