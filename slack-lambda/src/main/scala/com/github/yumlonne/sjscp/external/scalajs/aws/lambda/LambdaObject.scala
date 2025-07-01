package com.github.yumlonne.sjscp.external.scalajs.aws.lambda

import scalajs.js

object LambdaObject {
  @js.native
  trait ApiGatewayHttpEventV2 extends js.Object {
    val version: js.UndefOr[String]
    val routeKey: js.UndefOr[String]
    val rawPath: js.UndefOr[String]
    val rawQueryString: js.UndefOr[String]
    val cookies: js.UndefOr[js.Array[String]]
    val headers: js.UndefOr[js.Dictionary[String]]
    val queryStringParameters: js.UndefOr[js.Dictionary[String]]
    val requestContext: js.UndefOr[ApiGatewayRequestContextV2]
    val body: js.UndefOr[String]
    val pathParameters: js.UndefOr[js.Dictionary[String]]
    val isBase64Encoded: js.UndefOr[Boolean]
    val stageVariables: js.UndefOr[js.Dictionary[String]]
  }
  type Event = ApiGatewayHttpEventV2

  @js.native
  trait ApiGatewayRequestContextV2 extends js.Object {
    val accountId: js.UndefOr[String]
    val apiId: js.UndefOr[String]
    val domainName: js.UndefOr[String]
    val domainPrefix: js.UndefOr[String]
    val http: js.UndefOr[HttpContextV2]
    val requestId: js.UndefOr[String]
    val routeKey: js.UndefOr[String]
    val stage: js.UndefOr[String]
    val time: js.UndefOr[String]
    val timeEpoch: js.UndefOr[Double]
  }

  @js.native
  trait HttpContextV2 extends js.Object {
    val method: js.UndefOr[String]
    val path: js.UndefOr[String]
    val protocol: js.UndefOr[String]
    val sourceIp: js.UndefOr[String]
    val userAgent: js.UndefOr[String]
  }

  @js.native
  trait Context extends js.Object {
    val callbackWaitsForEmptyEventLoop: js.UndefOr[Boolean]
    val functionName: js.UndefOr[String]
    val functionVersion: js.UndefOr[String]
    val invokedFunctionArn: js.UndefOr[String]
    val memoryLimitInMB: js.UndefOr[String]
    val awsRequestId: js.UndefOr[String]
    val logGroupName: js.UndefOr[String]
    val logStreamName: js.UndefOr[String]
    val identity: js.UndefOr[js.Object]
    val clientContext: js.UndefOr[js.Object]
    val getRemainingTimeInMillis: js.UndefOr[js.Function0[Int]]
  }
}