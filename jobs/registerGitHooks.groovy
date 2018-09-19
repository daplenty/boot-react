
class Projects {
def static calls(String projectName) {
 withCredentials([string(credentialsId: 'gitpersonaltoken', variable: 'GITHUB_TOKEN')]) {
   println projectName 
 }
}
}
