#!/usr/bin/env bash


mkdir -p /etc/gu/
aws s3 cp s3://guconf-flexible/transcribe/transcribe.conf.dev /etc/gu/transcribe.conf --profile composer
