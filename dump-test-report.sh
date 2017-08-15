#!/usr/bin/env bash

if [ "$TRAVIS_REPO_SLUG" == "nlasagni/S3-16-we-drive-u" ]; then
  expect dump-test-report.exp $DEPLOY_USER $DEPLOY_PASS $DEPLOY_HOST $TRAVIS_JOB_NUMBER
fi