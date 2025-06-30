#!/bin/bash
set -ue

# sbt をほかのシェルで実行しておく必要あり
cd terraform/ && terraform apply -var-file=secret.tfvars -auto-approve
