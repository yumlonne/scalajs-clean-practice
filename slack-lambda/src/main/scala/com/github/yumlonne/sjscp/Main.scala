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
  val callType: Option[String] = for {
    body <- event.body.toOption
    _ = println(body)
    ev <- body.jsonGet[String]("event") // これが取れてないみたい
    _ = println(ev)
    t <- ev.jsonGet[String]("type")
    _ = println(t)
  } yield t

  val result: Future[js.Dynamic] = callType match {
    case Some("url_verification") => urlVerification(event)
    case Some("app_mention") => appMention(event)
    case _ => appMention(event) // ごまかし
  }

  result.toJSPromise
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


import com.github.yumlonne.sjscp.entity.*
import com.github.yumlonne.sjscp.application.gateway.*
import com.github.yumlonne.sjscp.application.presenter.*
import com.github.yumlonne.sjscp.application.usecase.*
import com.github.yumlonne.sjscp.adapter.*
import com.github.yumlonne.sjscp.external.implement.*
import com.github.yumlonne.sjscp.external.scalajs.awsclient.ec2
import scala.concurrent.ExecutionContext
def appMention(event: Event): Future[js.Dynamic] = {
  given ExecutionContext = scala.concurrent.ExecutionContext.global
  val res = for {
    //body <- event.body.toOption
    //text <- body.jsonGet[String]("text")
    //channel <- body.jsonGet[String]("channel")
    text <- Some("server list")
    channel <- Some("DUMMY")
  } yield {
    val slackBotUserId = js.Dynamic.global.process.env.asInstanceOf[js.Dictionary[String]].toMap.apply("SLACK_BOT_USER_ID")
    // app_mentionが来たのだから必ずbot_user_idは存在するはず
    //val tokens = text.trim.split("\\s+").toList.dropWhile(_ != s"<$slackBotUserId>").tail
    val tokens = (s"<$slackBotUserId>" + " " + text).trim.split("\\s+").toList.dropWhile(_ != s"<$slackBotUserId>").tail

    given ServerListView = new SlackServerListView()
    given ServerPresenter = new ServerListPresenter()
    given ec2.Client = new ec2.Client(region = "ap-northeast-1")
    given ServerGateway = new AwsEc2InstanceGateway()

    tokens match {
      case "server" :: args => new ServerController(args).run()
      case x => println(s"invalid input: $x"); Future.unit
    }
  }

  res.getOrElse(Future.unit).flatMap { _ =>
    Future.successful(
      js.Dynamic.literal(
        statusCode = 200,
      )
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