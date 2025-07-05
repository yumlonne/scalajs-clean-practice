package com.github.yumlonne.sjscp

import com.github.yumlonne.sjscp.adapter.*
import com.github.yumlonne.sjscp.application.presenter.*
import com.github.yumlonne.sjscp.application.gateway.*
import com.github.yumlonne.sjscp.external.implement.*
import com.github.yumlonne.sjscp.external.jvm.aws.ec2

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@main def main() = {
  given ExecutionContext = ExecutionContext.global
  given ServerListView = new ConsoleServerListView()
  given ServerListPresenter = new JPServerListPresenter()
  given ServerActionPresenter = new JPCatServerActionPresenter()
  given ServerGateway = new AwsEc2InstanceGateway(region = "ap-northeast-1")

  var next = true

  while (next) {
    print("> ")
    val input = scala.io.StdIn.readLine()
    val tokens = input.split("\\s+").filter(_ != "").toList
    tokens match {
      case "exit" :: _ =>
        next = false
      case "server" :: args =>
        Await.ready(new ServerController(args).run(), Duration.Inf)
      case x =>
        println(s"invalid input: $x")
    }
  }
}