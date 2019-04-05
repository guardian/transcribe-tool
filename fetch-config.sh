#!/usr/bin/env bash


mkdir -P /etc/gu/
aws s3 cp s3://guconf-flexible/transcribe/transcribe.conf /etc/gu/transcribe.conf --profile composer
