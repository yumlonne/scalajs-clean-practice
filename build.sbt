val scala3Version = "3.7.1"
val circeVersion = "0.14.6"

// 共通設定
ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "com.github.yumlonne.sjscp"

// sharedプロジェクト: lambdaとcliの両方で使用するロジックを定義
// crossProjectは不要。Scala.js単独プロジェクトにする
lazy val shared = project.in(file("shared"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    name := "shared",
    scalaJSUseMainModuleInitializer := false, // 起動用ではない
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client4" %%% "core" % "4.0.8",
      "io.circe"                      %%% "circe-core" % circeVersion,
      "io.circe"                      %%% "circe-generic" % circeVersion,
      "io.circe"                      %%% "circe-parser" % circeVersion,
      "org.scalameta"                 %%% "munit" % "1.0.0" % Test
    ),
  )

// slack-lambdaプロジェクト: AWS Lambda用のJSコードを出力
lazy val slackLambda = project.in(file("slack-lambda"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(shared)
  .settings(
    name := "slack-lambda",
    scalaJSUseMainModuleInitializer := false, // Lambdaは自動起動せず、exports.handlerで起動される
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client4" %%% "core" % "4.0.8",
    ),
    // XXX: Lambda環境にawssdkが入ってるのでサボる
    //Compile / npmDependencies ++= Seq(
    //  "@aws-sdk/client-ec2" -> "3.521.0",
    //),
  )

// cliプロジェクト: Node.jsでローカルCLI実行するためのプロジェクト
lazy val cli = project.in(file("cli"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(shared)
  .settings(
    name := "cli",
    scalaJSUseMainModuleInitializer := true, // @main関数を使って自動起動
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
    webpackBundlingMode := BundlingMode.Application,
    Compile / npmDependencies ++= Seq(
      "@aws-sdk/client-ec2" -> "3.521.0",
    ),
  )

// ルートプロジェクト（ビルド全体を管理）
lazy val root = project.in(file("."))
  .aggregate(shared, slackLambda, cli) // 各サブプロジェクトを集約
  .settings(
    name := "scalajs-clean-practice",
    publish / skip := true // パブリッシュしない設定
  )
