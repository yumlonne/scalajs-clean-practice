package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.entity.ServerInfo
import com.github.yumlonne.sjscp.application.gateway.ServerGateway
import com.github.yumlonne.sjscp.external.*

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.github.yumlonne.sjscp.entity.ServerActionResult

class AwsEc2InstanceGateway()(
  using
    ec2Client: scalajs.awsclient.ec2.Client,
    ec: ExecutionContext,
) extends ServerGateway {
  def list(): Future[List[ServerInfo]] = ec2Client.describeInstances()

  def start(id: String): Future[ServerActionResult] = ec2Client.startInstance(id)
  def stop(id: String): Future[ServerActionResult] = ec2Client.stopInstance(id)
}
