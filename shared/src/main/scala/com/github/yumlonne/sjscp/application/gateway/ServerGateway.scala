package com.github.yumlonne.sjscp.application.gateway

import com.github.yumlonne.sjscp.entity.ServerInfo
import com.github.yumlonne.sjscp.entity.ServerActionResult
import scala.concurrent.Future

trait ServerGateway {
  def list(): Future[List[ServerInfo]]

  def start(id: String): Future[ServerActionResult]
  def stop(id: String): Future[ServerActionResult]
}
