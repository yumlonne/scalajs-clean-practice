package com.github.yumlonne.sjscp.adapter

import com.github.yumlonne.sjscp.entity.ServerActionResult
import com.github.yumlonne.sjscp.entity.ServerActionResult.*
import com.github.yumlonne.sjscp.application.presenter.ServerActionPresenter

class JPCatServerActionPresenter()(
  using
    view: SimpleView,
) extends ServerActionPresenter {
  def showServerActionResult(serverActionResult: ServerActionResult): Unit = {
    val msg = serverActionResult match {
      case Success => "成功したニャ！"
      case ServerNotFound => "そんなの見つからないニャ"
      case AlreadyInExpectedState => "その操作をする必要はないみたいだニャ"
      case UnexpectedState(state) => s"操作できる状態じゃないみたいニャ～ ($state)"
      case UnexpectedError(message) => s"よくわかんにゃいにゃ～～！！！\n\n$message"
    }

    view.show(msg)
  }
}
