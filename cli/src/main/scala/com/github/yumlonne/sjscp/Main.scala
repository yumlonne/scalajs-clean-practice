package com.github.yumlonne.sjscp

import com.github.yumlonne.sjscp.entity.*
import com.github.yumlonne.sjscp.application.gateway.*
import com.github.yumlonne.sjscp.application.presenter.*
import com.github.yumlonne.sjscp.application.usecase.*
import com.github.yumlonne.sjscp.external.implement.*
import com.github.yumlonne.sjscp.external.scalajs.awsclient.ec2
import scala.concurrent.ExecutionContext

@main def main() = {
  given ExecutionContext = scala.concurrent.ExecutionContext.global
  given ConsoleView = new ConsoleView()
  given ServerListPresenter = new ServerListConsolePresenter()
  given ec2.Client = new ec2.Client(region = "ap-northeast-1")
  given ServerGateway = new AwsEc2InstanceGateway()

  val usecase = new ServerListUsecase()
  usecase.run()
}