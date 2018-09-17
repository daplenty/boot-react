import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import jenkins.model.Jenkins
import org.kohsuke.github.GHEvent
import org.kohsuke.github.GHHook

def call(String projectName) {
  withCredentials([string(credentialsId: 'gitpersonaltoken', variable: 'GITHUB_TOKEN')]) {
    String org = "daplenty"
    List<GHEvent> events = Arrays.asList(GHEvent.PUSH, GHEvent.PULL_REQUEST)
    GitHub github = GitHub.connectUsingOAuth(GITHUB_TOKEN)
    String rootUrl = Jenkins.getActiveInstance().getRootUrl()
    if (rootUrl == null) {
      return
    }
    GHRepository repo = github.getRepository("${org}/${projectName}")
    String url = rootUrl + "github-webhook/"
    try {
      if (!hookExists(repo, url)) {
        org.createWebHook(new URL(url), events)
      }
    }
    catch (Throwable e) {
      echo "Failed to register GitHub Repo Webhook for ${projectName}...Please check why... $e"
    }
  }
}

boolean hookExists(GHRepository repo, String url) throws IOException {
  for (GHHook hook : repo.getHooks()) {
    if (hook.getConfig().get("url").equals(url)) {
      return true
    }
  }
  return false
}
