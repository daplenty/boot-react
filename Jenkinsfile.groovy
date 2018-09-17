
  pipelineJob("PipelineTest") {

    description("Master Pipeline job to Upload artifacts to Artifactory")
    logRotator(-1, 10, -1, 1)
    quietPeriod(1)

    definition {
      cpsScm {
        scm {
          git {
            remote {
              github("daplenty/boot-react", 'https')
              credentials("githubpersonal")
              branch("master")
            }
            extensions {
              wipeOutWorkspace()
            }
          }
        }
        scriptPath("jenkins.gvy")
        lightweight()
      }
    }


    triggers {
      githubPush()
    }


    properties {
      githubProjectUrl('https://github.com/daplenty/boot-react')
    }

  }
