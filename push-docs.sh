#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "nlasagni/S3-16-we-drive-u" ] &&
   [ "$TRAVIS_PULL_REQUEST" == "false" ] &&
   [ "$TRAVIS_BRANCH" == "WDU_53_Travis_Setup_Enhancement" ]; then

  echo -e "Publishing docs...\n"

  cp -R latest-docs $HOME/latest-docs

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/nlasagni/S3-16-we-drive-u gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./java
  git rm -rf ./scala
  git rm -rf ./docs
  cp -Rf $HOME/javadoc-latest ./docs
  git add -f .
  git commit -m "Latest docs on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published docs to gh-pages.\n"
  
fi