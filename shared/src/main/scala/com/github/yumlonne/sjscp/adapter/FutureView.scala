package com.github.yumlonne.sjscp.adapter

import scala.concurrent.{Future, ExecutionContext}

// View実装が非同期になる場合、adapter/externalから完了を待つためのAPIを提供する
trait FutureView {
  def completed()(using ec: ExecutionContext): Future[Unit]
}
