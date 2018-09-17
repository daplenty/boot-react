#!groovy

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

