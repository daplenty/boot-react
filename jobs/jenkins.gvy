#!groovy

import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import jenkins.model.Jenkins
import org.kohsuke.github.GHEvent
import org.kohsuke.github.GHHook


pipeline {

  agent {
    node {
      label 'ecs-west-java-small'
    }
  }

  options {
    timeout(time: 1, unit: 'HOURS')
    timestamps()
    ansiColor('xterm')
  }

  stages {
    stage('Checkout Project') {
      steps {
        sh "pwd"
      }
    }
  }


}

