package com.github.yumlonne.sjscp.adapter

import scala.concurrent.Future

trait SimpleView {
  def show(s: String): Unit
}
