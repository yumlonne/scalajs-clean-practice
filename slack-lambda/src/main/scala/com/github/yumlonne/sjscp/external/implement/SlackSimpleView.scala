package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.adapter.SimpleView
import com.github.yumlonne.sjscp.adapter.FutureView
import com.github.yumlonne.sjscp.external.slack.SlackClient

import scala.concurrent.ExecutionContext
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class SlackSimpleView()(
  using
    slackClient: SlackClient,
) extends SimpleView with FutureView {
  private val futures: ListBuffer[Future[Unit]] = new ListBuffer()

  def show(s: String): Unit = {
    futures.addOne(slackClient.post(s))
  }

  def completed()(using ec: ExecutionContext): Future[Unit] = {
    Future.sequence(futures.toList).map(_ => ())
  }
}
