package com.github.yumlonne.sjscp.adapter

case class ServerListViewModel(
  headerMessage: Option[String],
  servers: List[List[(String, String)]],
)
