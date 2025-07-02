package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.adapter.ServerListViewModel
import com.github.yumlonne.sjscp.adapter.ServerListView
import com.github.yumlonne.sjscp.adapter.FutureView
import com.github.yumlonne.sjscp.external.slack.SlackClient
import com.github.yumlonne.sjscp.external.slack.blockkit.SlackBlockKit.*

import scala.concurrent.Future
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

extension[A] (list: List[A])
  def tailOpt: Option[List[A]] = list match {
    case _ :: t => Some(t)
    case Nil    => None
  }

class SlackServerListView()(
  using
    slackClient: SlackClient
) extends ServerListView with FutureView {
  private val futures: mutable.ListBuffer[Future[Unit]] = new ListBuffer()

  def show(s: String): Unit = {
    futures.addOne(slackClient.post(s))
  }

  def show(vm: ServerListViewModel): Unit = {
    val headerBlocks: BlockMessage = vm.headerMessage.map { h =>
      List(
        SectionBlock(text = Some(TextObject.plain(h))),
        Divider(),
      )
    }.getOrElse(Nil)

    val bodyBlocks: BlockMessage =
      vm.servers.flatMap { server =>
        List( // XXX: 各サーバーの区切りとしてDividerを要素間に挟む。最後にtailすることで先頭のDividerを削除している
          Divider(),
          SectionBlock(fields = Some(
            server.map { (k, v) =>
              TextObject.markdown(s"*$k*\n$v")
            }
          ))
        )
      }.tailOpt.getOrElse(Nil)

    futures.addOne(slackClient.postBlock(headerBlocks :++ bodyBlocks))
  }

  def showProcessing(): Unit = {
    futures.addOne(slackClient.reaction("eyes"))
  }

  def doneProcessing(): Unit = {
    futures.addOne(slackClient.reaction("white_check_mark"))
  }

  def completed()(using ec: ExecutionContext): Future[Unit] = {
    Future.sequence(futures.toList).map(_ => ())
  }
}
