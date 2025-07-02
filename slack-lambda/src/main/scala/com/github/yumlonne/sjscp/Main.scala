package com.github.yumlonne.sjscp

import com.github.yumlonne.sjscp.external.scalajs.aws.lambda.LambdaObject.*

import scalajs.js
import scala.scalajs.js.annotation.*
import scala.scalajs.js.JSConverters.JSRichFutureNonThenable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

@JSExportTopLevel(name = "handler", moduleID = "index")
def main(event: Event, context: Context): js.Promise[js.Dynamic] = {
  given ExecutionContext = ExecutionContext.global

  println(JSON.stringify(event))
  val slackEvent: Option[SlackEvent] = for {
    body <- event.body.toOption
    _ = println(body)
    ev <- body.jsonGet[SlackEvent]("event")
  } yield ev

  val res: Future[js.Dynamic] = slackEvent.map { ev =>
    ev.`type` match {
      case "url_verification" => urlVerification(event)
      case "app_mention" => appMention(ev)
      case _ => ???
    }
  }.getOrElse(throw new RuntimeException("can't parse event"))

  res.toJSPromise
}

def urlVerification(event: Event): Future[js.Dynamic] = {
  Future.successful(
    js.Dynamic.literal(
      statusCode = 200,
      headers = js.Dynamic.literal(
        "Content-Type" -> "application/json"
      ),
      body = JSON.stringify(js.Dynamic.literal(
        "challenge" -> event.body.toOption.flatMap(_.jsonGet[String]("challenge")).getOrElse(throw new RuntimeException("!!!"))
      ))
    )
  )
}

import io.circe.*, io.circe.parser.*
import io.circe.generic.semiauto.*
import com.github.yumlonne.sjscp.external.slack.SlackClient
case class SlackEvent(
  `type`: String,
  text: String,
  channel: String,
  event_ts: String,
)
given Decoder[SlackEvent] = deriveDecoder[SlackEvent]


import com.github.yumlonne.sjscp.entity.*
import com.github.yumlonne.sjscp.application.gateway.*
import com.github.yumlonne.sjscp.application.presenter.*
import com.github.yumlonne.sjscp.application.usecase.*
import com.github.yumlonne.sjscp.adapter.*
import com.github.yumlonne.sjscp.external.implement.*
import com.github.yumlonne.sjscp.external.scalajs.awsclient.ec2
import scala.concurrent.ExecutionContext
def appMention(event: SlackEvent): Future[js.Dynamic] = {
  given ExecutionContext = scala.concurrent.ExecutionContext.global

  // TODO: envvar定義
  val slackBotUserId = js.Dynamic.global.process.env.asInstanceOf[js.Dictionary[String]].toMap.apply("SLACK_BOT_USER_ID")
  val slackBotToken = js.Dynamic.global.process.env.asInstanceOf[js.Dictionary[String]].toMap.apply("SLACK_BOT_TOKEN")
  // app_mentionが来たのだから必ずbot_user_idは存在するはず
  val tokens = event.text.trim.split("\\s+").toList.dropWhile(_ != s"<$slackBotUserId>").tail

  given SlackClient = new SlackClient(slackBotToken, event.channel, event.event_ts)
  given view: SlackServerListView = new SlackServerListView()
  given ServerPresenter = new ServerListPresenter()
  given ec2.Client = new ec2.Client(region = "ap-northeast-1")
  given ServerGateway = new AwsEc2InstanceGateway()

  val res = tokens match {
    case "server" :: args => new ServerController(args).run()
    case x => println(s"invalid input: $x"); Future.unit
  }

  for {
    _ <- view.completed()
    _ <- res
  } yield {
    js.Dynamic.literal(
      statusCode = 200,
    )
  }
}


import io.circe.*, io.circe.parser.*
import scala.scalajs.js.JSON
def getEventType(event: Event): Option[String] = {
  for {
    body <- event.body.toOption
    parsed <- parse(body).toOption
    `type` <- parsed.hcursor.get[String]("type").toOption
  } yield `type`
}

extension (s: String)
  def jsonGet[A](key: String)(using decoder: Decoder[A]): Option[A] =
    for {
      parsed <- parse(s).toOption
      result <- parsed.hcursor.get[A](key).toOption
    } yield result