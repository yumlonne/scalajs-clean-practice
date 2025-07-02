package com.github.yumlonne.sjscp.external.slack.blockkit

import io.circe.Json
import io.circe.syntax.*
import io.circe.Encoder
import io.circe.generic.semiauto.*

object SlackBlockKit {
  sealed trait Block {
    def `type`: String
    def toJson: Json
  }
  case class SectionBlock(
    text: Option[TextObject] = None,
    fields: Option[List[TextObject]] = None,
  ) extends Block {
    val `type` = "section"
    def toJson: Json = this.asJson.deepMerge(Json.obj("type" -> Json.fromString(`type`))).dropNullValues
  }
  case class Divider() extends Block {
    val `type` = "divider"
    def toJson: Json = Json.obj("type" -> Json.fromString(`type`))
  }

  case class TextObject(`type`: String, text: String)
  object TextObject {
    def markdown(text: String) = TextObject("mrkdwn", text)
    def plain(text: String) = TextObject("plain_text", text)
  }

  type BlockMessage = List[Block]

  given Encoder[Block] = Encoder.instance(_.toJson)
  given Encoder[SectionBlock] = deriveEncoder
  given Encoder[Divider] = deriveEncoder
  given Encoder[TextObject] = deriveEncoder
}