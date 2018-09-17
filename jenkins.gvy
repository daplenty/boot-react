#!groovy

@Library('shared@BATOPS-716-Notifying-Commit-Author-About-Failed-API-Tests') _

pipeline {

  agent {
    node {
      label env.labelName
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
        git branch: env.branchName, credentialsId: env.github_creds, url: "git@github.com:wkda/${env.projectName}.git"
      }
    }
    stage('Maven Clean Verify') {
      steps {
        withMaven(options: [artifactsPublisher(disabled: true)], globalMavenSettingsConfig: 'maven-global', maven: 'maven-3.3.9') {
          echo "mvn clean verify ${env.profiles}"
        }
      }
    }
    stage('Fetch Swagger Docs') {
      steps {
        script {

          if (findFiles(glob: 'service/target/generated-docs/swagger*.json')) {
            echo "mkdir \${WORKSPACE}/target"
            echo "cp -r \${WORKSPACE}/service/target/generated-docs \${WORKSPACE}/target/"
          }
        }
      }
    }
    stage('Upload Swagger Docs') {
      steps {
        script {
          if (findFiles(glob: 'target/generated-docs/swagger*.json')) {
            echo "cp \${WORKSPACE}/target/generated-docs/swagger*.json /srv/www/services/htdocs/swagger-${projectName}.json || :"
            echo "cp \${WORKSPACE}/target/generated-docs/swagger*.viz /srv/www/services/htdocs/swagger-${projectName}.viz || :"
            echo "sed -i 's/\"host\":\"[^\"]*\",/\"host\":\"swagger.qa.auto1.team\",/g' /srv/www/services/htdocs/swagger-${projectName}.json || :"
            echo "cp \${WORKSPACE}/target/generated-docs/swagger*.json \${WORKSPACE}/target/generated-docs/swagger-${projectName}.json || :"
            echo "sed -i 's/\"host\":\"[^\"]*\",/\"host\":\"\\/\",/g' \${WORKSPACE}/target/generated-docs/swagger-${projectName}.json || :"

            withMaven(globalMavenSettingsConfig: 'maven-global', maven: 'maven-3.3.9') {
              echo "mvn deploy:deploy-file -DrepositoryId=w-snapshots " +
                "-Durl=https://artifactory.prod.auto1.team/artifactory/services/  " +
                "-DpomFile=${env.pomFile} -Dclassifier=swagger " +
                "-Dfile=target/generated-docs/swagger-${env.projectName}.json -Dpackaging=json"
            }
          }
        }
      }
    }
    stage('Deploy to Artifactory') {
      steps {
        script {
          withMaven(options: [artifactsPublisher(disabled: true)], globalMavenSettingsConfig: 'maven-global', maven: 'maven-3.3.9') {
            def extraArgs = projectName.startsWith("commons-") ? "" : "-Dmaven.source.skip=true"
            echo "mvn deploy -Dmaven.main.skip=true ${extraArgs} -Dmaven.git-commit-id.skip=true -DskipTests=true"
          }
        }
      }
    }
    stage('Trigger Java Docker Build') {
      steps {
        script {
          echo "dockerb"
        }
      }
    }
    stage('Trigger Project Docker Build') {
      steps {
        script {
          if (env.ecsProjectKey?.trim()) {
            echo "projectsuite"

            if (env.ecsTestSuiteValue?.trim()) {
              echo "testsuite"
            }
          }
        }
      }
    }
  }

  post {
    failure {
      script {
        sendJobErrorNotifications("failure",getErrMessage(),"#javaopstest")
      }
    }
    unstable {
      script {
        sendJobErrorNotifications("unstable",getErrMessage(),"#javaopstest")
      }
    }
    fixed {
      script {
        sendJobErrorNotifications("fixed",getErrMessage(),"#javaopstest")
      }
    }
    success {
      script {
        build job: env.downstreamJob, parameters: [[$class: 'TextParameterValue', name: 'commit_authors', value: getListOfCommitAuthors()]], wait: false
      }
    }

  }
}

String getErrMessage() {
  return "${env.projectName} encountered some errors and could not complete."
}
