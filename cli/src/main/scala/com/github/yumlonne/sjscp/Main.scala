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
  given ec2.Client = new ec2.Client(region = "ap-northeast-1")
  given ServerGateway = new AwsEc2InstanceGateway()

  mainLoop { input =>
    val tokens = input.split("\\s+").filter(_ != "").toList
    tokens match {
      case "exit" :: _ => Future(false)
      case "server" :: args => new ServerController(args).run().map(_ => true)
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
import com.github.yumlonne.sjscp.adapter.ServerController
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