val scala3Version = "3.7.1"
val circeVersion = "0.14.6"

// 共通設定
ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "com.github.yumlonne.sjscp"

// sharedプロジェクト: lambdaとcliの両方で使用するロジックを定義
lazy val shared = project.in(file("shared"))
  .settings(
    name := "shared",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client4" %%% "core" % "4.0.8",
      "io.circe"                      %%% "circe-core" % circeVersion,
      "io.circe"                      %%% "circe-generic" % circeVersion,
      "io.circe"                      %%% "circe-parser" % circeVersion,
      "org.scalameta"                 %%% "munit" % "1.0.0" % Test
    ),
  )
lazy val sharedScalaJS = project.in(file("shared-scalajs"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(shared)
  .settings(
    name := "shared-scalajs",
    scalaJSUseMainModuleInitializer := false,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
  )

// slack-lambdaプロジェクト: AWS Lambda用のJSコードを出力
// sbt slackLambda/fastOptJS でjsを生成
lazy val slackLambda = project.in(file("slack-lambda"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(sharedScalaJS)
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
// sbt cli/fastOptJS でjsを生成
lazy val cli = project.in(file("cli"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(sharedScalaJS)
  .settings(
    name := "cli",
    scalaJSUseMainModuleInitializer := true, // @main関数を使って自動起動
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
    webpackBundlingMode := BundlingMode.Application,
    Compile / npmDependencies ++= Seq(
      "@aws-sdk/client-ec2" -> "3.521.0",
    ),
  )

// cliプロジェクト: JVMでローカルCLI実行するためのプロジェクト
// sbt cliJvm/assembly でjarを生成
lazy val cliJvm = project.in(file("cli-jvm"))
  .dependsOn(shared)
  .settings(
    name := "cli-jvm",
    libraryDependencies ++= Seq(
      "com.github.seratch" %%% "awscala-ec2" % "0.9.+",
    ),
    assembly / assemblyJarName := "jvmcli.jar"
  )

// ルートプロジェクト（ビルド全体を管理）
lazy val root = project.in(file("."))
  .aggregate(shared, slackLambda, cli, cliJvm) // 各サブプロジェクトを集約
  .settings(
    name := "scalajs-clean-practice",
    publish / skip := true // パブリッシュしない設定
  )
