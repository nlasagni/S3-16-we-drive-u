#!/usr/bin/env bash

if [ "$TRAVIS_REPO_SLUG" == "nlasagni/S3-16-we-drive-u" ] &&
   [ "$TRAVIS_PULL_REQUEST" == "false" ] &&
   [ "$TRAVIS_BRANCH" == "WDU_53_Travis_Setup_Enhancement" ]; then
  expect deploy-services.exp
fi