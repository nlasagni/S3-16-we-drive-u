#!/usr/bin/env bash

if [ "$TRAVIS_REPO_SLUG" == "nlasagni/S3-16-we-drive-u" ] &&
   [ "$TRAVIS_PULL_REQUEST" == "false" ] &&
   [ "$TRAVIS_BRANCH" == "master" ]; then
  expect deploy-services.exp $DEPLOY_USER $DEPLOY_PASS $DEPLOY_HOST $TRAVIS_JOB_NUMBER
fi