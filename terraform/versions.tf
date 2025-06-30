terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.99"
    }
  }

  required_version = ">= 1.12.0"

  backend "s3" {
    bucket       = "terraform-state-810413939019"
    key          = "scalajs-clean-practice.terraform.tfstate"
    region       = "ap-northeast-1"
    use_lockfile = true
  }
}

provider "aws" {
  region = "ap-northeast-1"

  default_tags {
    tags = {
      "ManagedBy"  = "Terraform"
      "Repository" = "https://github.com/yumlonne/scalajs-clean-practice"
    }
  }
}

# terraform destroyを防ぐ
resource "terraform_data" "destroy_guard" {
  lifecycle {
    prevent_destroy = true
  }
}