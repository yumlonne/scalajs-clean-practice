package com.github.yumlonne.sjscp.application.gateway

import com.github.yumlonne.sjscp.entity.ServerInfo
import scala.concurrent.Future

trait ServerGateway {
  def list(): Future[List[ServerInfo]]

  // 失敗したらメッセージが返る
  def start(id: String): Future[Option[String]]
  def stop(id: String): Future[Option[String]]
}
