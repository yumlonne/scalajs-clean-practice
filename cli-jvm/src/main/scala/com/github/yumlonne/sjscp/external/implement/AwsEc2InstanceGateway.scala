package com.github.yumlonne.sjscp.external.implement

import com.github.yumlonne.sjscp.application.gateway.ServerGateway
import scala.concurrent.Future
import com.github.yumlonne.sjscp.entity.ServerInfo
import com.github.yumlonne.sjscp.entity.ServerActionResult

import scala.concurrent.ExecutionContext
given ExecutionContext = scala.concurrent.ExecutionContext.global

import awscala.*
import awscala.ec2.*
import scala.collection.immutable.ListMap
import com.github.yumlonne.sjscp.entity.ServerState



class AwsEc2InstanceGateway(val region: String) extends ServerGateway {

  lazy val ec2 = EC2.at(Region(region))
  def list(): Future[List[ServerInfo]] = Future {
    val instances = ec2.instances
    instances.map { instance =>
      new ServerInfo {
        def id = instance.instanceId
        def name = instance.getName.getOrElse("-")
        def state = { instance.state.getName match {
          case "running" => ServerState.Running
          case "stopped" => ServerState.Stopped
          case other     => ServerState.Indifferent(other)
        } }
        def spec = instance.instanceType
        def extra = instance.tags
        def toMap: Map[String, String] = ListMap(
          "ID" -> id,
          "Name" -> name,
          "State" -> state.toString,
          "InstanceTyoe" -> spec,
        )
      }
    }.toList
  }
  def start(id: String): Future[ServerActionResult] = Future {
    val res = ec2.start(getInstanceById(id))
    ServerActionResult.Success // XXX
  }
  def stop(id: String): Future[ServerActionResult] = Future {
    val res = ec2.stop(getInstanceById(id))
    ServerActionResult.Success // XXX
  }

  private def getInstanceById(id: String): awscala.ec2.Instance = {
    ec2.instances(id)(0)
  }
}
