#!/bin/bash

# Get Project Repo
aspectran_repo=$(git config --get remote.origin.url 2>&1)
echo "Repo detected: ${aspectran_repo}"

# Get Commit Message
commit_message=$(git log --format=%B -n 1)
echo "Current commit detected: ${commit_message}"

# Get the Java version.
# Java 1.5 will give 15.
# Java 1.6 will give 16.
# Java 1.7 will give 17.
# Java 1.8 will give 18.
VER=`java -version 2>&1 | sed 's/java version "\(.*\)\.\(.*\)\..*"/\1\2/; 1q'`
echo "Java detected: ${VER}"

# We parser for several JDKs on Travis.
# Some actions, like analyzing the code (Coveralls) and uploading
# artifacts on a Maven repository, should only be made for one version.

if [ "$aspectran_repo" == "https://github.com/aspectran/aspectran.git" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ] && [[ "$commit_message" != *"[maven-destroy-plugin]"* ]]; then
  if [ $VER == "18" ]; then
    mvn clean deploy -Dmaven.test.skip=true -q --settings ./travis/settings.xml
    echo -e "Successfully deployed SNAPSHOT artifacts to Sonatype under Travis job ${TRAVIS_JOB_NUMBER}"
    mvn clean test jacoco:report coveralls:report -q
    echo -e "Successfully ran coveralls under Travis job ${TRAVIS_JOB_NUMBER}"
    # various issues exist currently in building this so comment for now
    # mvn site site:deploy -q
    # echo -e "Successfully deploy site under Travis job ${TRAVIS_JOB_NUMBER}"
    # mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=ccf0be39fd0ca5ea5aa712247c79da7233cd3caa
    # echo -e "Successfully ran Sonar integration under Travis job ${TRAVIS_JOB_NUMBER}"
  fi
else
  echo "Travis parser skipped"
fi
