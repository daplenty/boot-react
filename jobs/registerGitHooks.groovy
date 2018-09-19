
class Projects {
def static calls(String projectName) {
  List<org.kohsuke.github.GHEvent> events = Arrays.asList(org.kohsuke.github.GHEvent.PUSH, org.kohsuke.github.GHEvent.PULL_REQUEST)
   println projectName 
}
}
