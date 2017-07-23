#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "nlasagni/S3-16-we-drive-u" ] &&
   [ "$TRAVIS_PULL_REQUEST" == "false" ] &&
   [ "$TRAVIS_BRANCH" == "WDU_53_Travis_Setup_Enhancement" ]; then

  echo -e "Deploying services...\n"
  rm -rf $HOME/services
  cp -R releases/services $HOME/services
  cd $HOME
  export SSHPASS=$DEPLOY_PASS
  sshpass -e scp services $DEPLOY_USER@$DEPLOY_HOST:$DEPLOY_PATH/deploy-$TRAVIS_BUILD_NUMBER
  echo -e "Services deployed.\n"
fi