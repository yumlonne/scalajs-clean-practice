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

  import scala.scalajs.js.JSON // debug
  def startInstance(instanceId: String)(using ec: ExecutionContext): Future[Option[String]] = {
    val command = new Facade.StartInstancesCommand(js.Dictionary(
      "InstanceIds" -> js.Array(instanceId)
    ))

    client.send(command).toFuture.map { v => 
      val dynamic = v.asInstanceOf[js.Dynamic]
      val instance = dynamic.StartingInstances.asInstanceOf[js.Array[js.Dynamic]].toList.head
      val currentState = instance.CurrentState.Name.asInstanceOf[String]
      val previousState = instance.PreviousState.Name.asInstanceOf[String]

      if (previousState == currentState)
        Some(s"State '${previousState}' not changed!!")
      else 
        None
    }.recover {
      case e =>
        Some(e.getStackTrace().map(_.toString).mkString("\n"))
    }
  }

  def stopInstance(instanceId: String)(using ec: ExecutionContext): Future[Option[String]] = {
    val command = new Facade.StopInstancesCommand(js.Dictionary(
      "InstanceIds" -> js.Array(instanceId)
    ))

    client.send(command).toFuture.map { v =>
      val dynamic = v.asInstanceOf[js.Dynamic]
      val instance = dynamic.StoppingInstances.asInstanceOf[js.Array[js.Dynamic]].toList.head
      val currentState = instance.CurrentState.Name.asInstanceOf[String]
      val previousState = instance.PreviousState.Name.asInstanceOf[String]

      if (previousState == currentState)
        Some(s"State '${previousState}' not changed!!")
      else 
        None
    }.recover {
      case e =>
        Some(e.getStackTrace().map(_.toString).mkString("\n"))
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

import io.circe.*
import io.circe.generic.semiauto.*

private case class InstanceState(
  Code: Int,
  Name: String
)

private case class InstanceTransition(
  InstanceId: String,
  PreviousState: InstanceState,
  CurrentState: InstanceState
)

given Decoder[InstanceState] = deriveDecoder
given Decoder[InstanceTransition] = deriveDecoder