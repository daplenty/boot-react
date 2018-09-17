
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

    configure { project ->
      def scriptContainers = project / 'properties' / 'hudson.model.ParametersDefinitionProperty' / 'parameterDefinitions'
      scriptContainers.each { org_level ->
        def script_level = org_level / 'script'
        script_level.appendNode('secureScript', [plugin: 'script-security@1.40']).with {
          appendNode('sandbox', 'true')
          appendNode('script', script_level.script.text())
          script_level.remove(script_level / 'script')
        }
        script_level.appendNode('secureFallbackScript', [plugin: 'script-security@1.40']).with {
          appendNode('sandbox', 'true')
          appendNode('script', script_level.fallbackScript.text())
          script_level.remove(script_level / 'fallbackScript')
        }
      }
    }
  }
