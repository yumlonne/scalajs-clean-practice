package com.github.yumlonne.sjscp.application.presenter

import com.github.yumlonne.sjscp.entity.ServerActionResult

// サーバーに対するアクション結果の表示
trait ServerActionPresenter {
  def showServerActionResult(serverActionResult: ServerActionResult): Unit
}