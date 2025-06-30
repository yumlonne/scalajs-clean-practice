resource "aws_iam_role" "lambda_slackbot" {
  name = "lambda_slackbot_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_slackbot_basic_execution" {
  role       = aws_iam_role.lambda_slackbot.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy" "allow_ec2_operation" {
  name = "allow_ec2_operation"
  role = aws_iam_role.lambda_slackbot.name

  policy = data.aws_iam_policy_document.allow_ec2_operation.json
}

data "aws_iam_policy_document" "allow_ec2_operation" {
  statement {
    sid = "AllowDescribeInstances"
    actions = [
      "ec2:DescribeInstances",
    ]
    effect    = "Allow"
    resources = ["*"]
  }
  statement {
    sid = "AllowStartStopInstances"
    actions = [
      "ec2:StartInstances",
      "ec2:StopInstances",
    ]
    effect    = "Allow"
    resources = ["*"]
  }
}

resource "terraform_data" "build" {
  triggers_replace = [
    sha1(
      join("", concat(
        [for f in fileset("../shared/src/main/scala", "**/*") : filesha1("../shared/src/main/scala/${f}")],
        [for f in fileset("../lambda/src/main/scala", "**/*") : filesha1("../lambda/src/main/scala/${f}")],
      )),
    ),
  ]

  provisioner "local-exec" {
    command = "cd ../ && sbtn lambda/fastOptJS && cd -"
  }
}

data "archive_file" "this" {
  type        = "zip"
  source_file = "../lambda/target/scala-3.7.1/lambda-fastopt/index.js"
  output_path = "lambda.zip"

  depends_on = [terraform_data.build]
}


resource "aws_lambda_function" "this" {
  function_name    = "scalajs-clean-slackbot"
  role             = aws_iam_role.lambda_slackbot.arn
  handler          = "index.handler"
  runtime          = "nodejs20.x"
  memory_size      = 1024 # コールドスタート時の処理が遅すぎる問題対策で多めにしている。最低512MB
  timeout          = 3
  source_code_hash = data.archive_file.this.output_base64sha256
  filename         = data.archive_file.this.output_path

  environment {
    variables = {
      SLACK_BOT_TOKEN      = var.slack_bot_token
      SLACK_BOT_USER_ID    = var.slack_bot_user_id
      SLACK_SIGNING_SECRET = var.slack_signing_secret
    }
  }

  depends_on = [aws_iam_role_policy_attachment.lambda_slackbot_basic_execution]
}


###### request routing #####
resource "aws_apigatewayv2_api" "this" {
  name          = "scalajs_clean_slackbot"
  protocol_type = "HTTP"
}

resource "aws_lambda_permission" "allow_apigateway" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.this.function_name
  principal     = "apigateway.amazonaws.com"

  # The source ARN is the API Gateway's execution ARN
  source_arn = "${aws_apigatewayv2_api.this.execution_arn}/*/*"
}

resource "aws_apigatewayv2_integration" "this" {
  api_id           = aws_apigatewayv2_api.this.id
  integration_type = "AWS_PROXY"

  connection_type    = "INTERNET"
  description        = "Integration for Slack Chatbot Lambda Function"
  integration_method = "POST"
  integration_uri    = aws_lambda_function.this.invoke_arn
}

resource "aws_apigatewayv2_route" "this" {
  api_id    = aws_apigatewayv2_api.this.id
  route_key = "POST /slack/test"

  target = "integrations/${aws_apigatewayv2_integration.this.id}"
}

resource "aws_apigatewayv2_stage" "this" {
  api_id = aws_apigatewayv2_api.this.id
  name   = "$default"

  auto_deploy = true

  lifecycle {
    create_before_destroy = true
  }
}