package com.github.yumlonne.sjscp.external.scalajs.awsclient.ec2

import com.github.yumlonne.sjscp.entity.*
import com.github.yumlonne.sjscp.external.scalajs.awsclient.ec2.Facade

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scalajs.js

// js依存だと使いずらいのでScalaっぽくする君
class Client(region: String) {
  private lazy val client = new Facade.Ec2Client(js.Dictionary(
    "region" -> region,
  ))

  def describeInstances()(using ec: ExecutionContext): Future[List[ServerInfo]] = {
    val command = new Facade.DescribeInstancesCommand(js.Dictionary())

    client.send(command).toFuture.map { result =>
      val resJs = result.asInstanceOf[js.Dynamic]
      val reservations = resJs.selectDynamic("Reservations").asInstanceOf[js.UndefOr[js.Array[js.Dynamic]]]

      reservations.toOption
        .getOrElse(js.Array())
        .flatMap { res =>
          val instances = res.selectDynamic("Instances").asInstanceOf[js.UndefOr[js.Array[js.Dynamic]]].toOption.getOrElse(js.Array())
          instances.map { inst =>
            val tags = inst.selectDynamic("Tags").asInstanceOf[js.UndefOr[js.Array[js.Dynamic]]]
              .toOption
              .getOrElse(js.Array())
              .flatMap { tag =>
                for {
                  key <- tag.selectDynamic("Key").asInstanceOf[js.UndefOr[String]].toOption
                  value <- tag.selectDynamic("Value").asInstanceOf[js.UndefOr[String]].toOption
                } yield key -> value
              }.toMap

            AwsEC2ServerInfo(
              id = inst.selectDynamic("InstanceId").asInstanceOf[js.UndefOr[String]].getOrElse("?"),
              name = tags.getOrElse("Name", "-"),
              stateStr = inst.selectDynamic("State").selectDynamic("Name").asInstanceOf[js.UndefOr[String]].getOrElse("?"),
              spec = inst.selectDynamic("InstanceType").asInstanceOf[js.UndefOr[String]].getOrElse("?"),
              extra = tags.filterKeys(_ != "Name").toMap,
            )
          }.toList
        }.toList
      }
  }
}

case class AwsEC2ServerInfo(
  id: String,
  name: String,
  spec: String,
  extra: Map[String, String],
  stateStr: String,
) extends ServerInfo {
  val state: ServerState = stateStr match {
    case "running" => ServerState.Running
    case "stopped" => ServerState.Stopped
    case s         => ServerState.Indifferent(s)
  }
}
