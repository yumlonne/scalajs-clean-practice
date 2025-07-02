package com.github.yumlonne.sjscp.external.slack

import com.github.yumlonne.sjscp.external.slack.blockkit.SlackBlockKit.BlockMessage

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext
import sttp.client4.*
import sttp.client4.fetch.FetchBackend

import io.circe.syntax.*
import io.circe.*, io.circe.parser.*
import io.circe.generic.semiauto.*

class SlackClient(
  val botToken: String,
  val channelId: String,
  val timestamp: String, // 起動原因になったメッセージのタイムスタンプ
)(
  using
    ec: ExecutionContext
) {

  private lazy val backend = FetchBackend()

  def post(msg: String): Future[Unit] = {
    val uri = uri"https://slack.com/api/chat.postMessage"

    val payload = SlackClient.PostMessagePayload(
      channel = channelId,
      text = msg,
    )

    basicRequest.post(uri)
      .header("Content-Type", "application/json")
      .header("Authorization", s"Bearer $botToken")
      .body(payload.asJson.noSpaces)
      .send(backend)
      .map(_ => ())
  }

  def postBlock(blockMessage: BlockMessage): Future[Unit] = {
    val uri = uri"https://slack.com/api/chat.postMessage"

    val payload = SlackClient.BlockMessagePayload(
      channel = channelId,
      blocks = blockMessage,
    )

    basicRequest.post(uri)
      .header("Content-Type", "application/json")
      .header("Authorization", s"Bearer $botToken")
      .body(payload.asJson.noSpaces)
      .send(backend)
      .map(_ => ())
  }

  // メッセージにリアクションをつける
  // timestampからメッセージを特定する
  def reaction(emoji: String): Future[Unit] = {
    // https://api.slack.com/methods/reactions.add
    val uri = uri"https://slack.com/api/reactions.add"

    val payload = SlackClient.ReactionPayload(
      channel = channelId,
      name = emoji,
      timestamp = timestamp,
    )

    basicRequest.post(uri)
      .header("Content-Type", "application/json")
      .header("Authorization", s"Bearer $botToken")
      .body(payload.asJson.noSpaces)
      .send(backend)
      .map(_ => ())
  }
}

private object SlackClient {
  case class PostMessagePayload(
    channel: String,
    text: String,
  )
  given Encoder[PostMessagePayload] = deriveEncoder

  case class BlockMessagePayload(
    channel: String,
    blocks: BlockMessage,
  )
  given Encoder[BlockMessagePayload] = deriveEncoder

  case class ReactionPayload(
    channel: String,
    name: String,
    timestamp: String
  )
  given Encoder[ReactionPayload] = deriveEncoder
}
