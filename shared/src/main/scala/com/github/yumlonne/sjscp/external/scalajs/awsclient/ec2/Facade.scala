package com.github.yumlonne.sjscp.external.scalajs.awsclient.ec2

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// jsモジュールのaws-sdkを使うためのファサード
object Facade {
  @js.native
  @JSImport("@aws-sdk/client-ec2", "EC2Client")
  class Ec2Client(config: js.Dictionary[String]) extends js.Object {
    def send(command: js.Any): js.Promise[js.Any] = js.native
  }

  @js.native
  @JSImport("@aws-sdk/client-ec2", "DescribeInstancesCommand")
  class DescribeInstancesCommand(input: js.Dictionary[js.Any]) extends js.Object

  @js.native
  @JSImport("@aws-sdk/client-ec2", "StartInstancesCommand")
  class StartInstancesCommand(input: js.Dictionary[js.Any]) extends js.Object

  @js.native
  @JSImport("@aws-sdk/client-ec2", "StopInstancesCommand")
  class StopInstancesCommand(input: js.Dictionary[js.Any]) extends js.Object
}