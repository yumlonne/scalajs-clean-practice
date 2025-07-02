package com.github.yumlonne.sjscp.external.scalajs

import scalajs.js

class EnvironmentVariable {
  private lazy val envVar: Map[String, String] = js.Dynamic.global.process.env.asInstanceOf[js.Dictionary[String]].toMap

  val getOpt: String => Option[String] = envVar.get
}
