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
  given ServerPresenter = new ServerConsolePresenter()
  given client: ec2.Client = new ec2.Client(region = "ap-northeast-1")
  given ServerGateway = new AwsEc2InstanceGateway()

  val serverUsecase = new ServerUsecase()

  mainLoop { input =>
    val tokens = input.split("\\s+").filter(_ != "").toList
    tokens match {
      case "exit" :: _ => Future(false)
      case "show" :: _ => serverUsecase.list().map(_ => true)
      // XXX: 仮実装 clientを直接使うな!!
      //case "start" :: id :: _ => client.startInstance(id).map{ res => println(res.toString); true }
      //case "stop" :: id :: _ => client.stopInstance(id).map{ res => println(res.toString); true }
      case "start" :: id :: _ => serverUsecase.start(id).map(_ => true)
      case "stop" :: id :: _ => serverUsecase.stop(id).map(_ => true)
      case x => println(s"invalid input: $x"); Future(true)
    }
  }
}

def mainLoop(f: String => Future[Boolean])(using ec: ExecutionContext): Unit = {
  readLine("> ").foreach { input =>
    f(input).foreach{ nextLoop =>
      if (nextLoop) mainLoop(f)
    }
  }
}


import scalajs.js
import scalajs.js.Dynamic.global
import scala.concurrent.{Future, Promise}
def readLine(prompt: String = ""): Future[String] = {
  val readline = global.require("readline")
  val rl = readline.createInterface(
    js.Dynamic.literal(
      input = global.process.stdin,
      output = global.process.stdout
    )
  )

  val p = Promise[String]()
  if (prompt.nonEmpty) global.process.stdout.write(prompt)

  rl.on("line", (line: js.Any) => {
    val input = line.toString.trim
    rl.close() // 終了させる
    p.success(input)
  })

  rl.on("error", (e: js.Any) => {
    rl.close()
    p.failure(new Exception(e.toString))
  })

  p.future
}