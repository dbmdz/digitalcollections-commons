node {
	env.JAVA_HOME = tool 'jdk-8-oracle'
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"

  try {
    notifyBuild("STARTED");
    stage 'Build'
    checkout scm
    sh 'mvn -U clean compile'

    stage 'Verify'
    sh 'mvn verify'

    stage 'Quality'
    sh 'mvn -Dmaven.test.failure.ignore=false -P sonar sonar:sonar'

    stage 'Publish'
    sh 'mvn -T 4 deploy -Dmaven.install.skip=true'
    notifyBuild("SUCCESS");
  }
  catch (e) {
    notifyBuild("FAILED");
  }
  
}

def notifyBuild(String buildStatus = 'UNKNOWN') {
  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "[Jenkins] ${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = """
    <p>${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>
    """

  if (buildStatus == "FAILED") {
    subject += " 🕱 💀 ☠ 😓 😟"
  }
  else if (buildStatus == "SUCCESS") {
    subject += " 🙋 🙌"
  }

  emailext(
      subject: subject,
      body: details,
      recipientProviders: [[$class: 'DevelopersRecipientProvider']]
  )

}
