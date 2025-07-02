package com.github.yumlonne.sjscp.external.slack

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext
import sttp.client4.*
import sttp.client4.fetch.FetchBackend

import io.circe.syntax.*
import io.circe.*, io.circe.parser.*
import io.circe.generic.semiauto.*

class SlackClient(
  val channelId: String,
  val token: String,
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
      .header("Authorization", s"Bearer $token")
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
}
