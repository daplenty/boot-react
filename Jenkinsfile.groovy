
  pipelineJob("PipelineTest") {

    description("Master Pipeline job to Upload artifacts to Artifactory")
    logRotator(-1, 10, -1, 1)
    quietPeriod(1)

    scm {
      git {
        remote {
          // Here to trigger on GitHub commit
          github("daplenty/boot-react", 'https')
          credentials("github-ssh-rw-key")
          branch("master")
        }
        extensions {
          wipeOutWorkspace()
        }
      }
    }

    definition {
      cps {
        sandbox()
        script(readFileFromWorkspace("jenkins.gvy"))
      }
    }

    triggers {
      githubPush()
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
