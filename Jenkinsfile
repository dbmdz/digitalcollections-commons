node {
	env.JAVA_HOME = tool 'jdk-8-oracle'
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"

  try {
    notifyBuild "STARTED"
    stage 'Build'
    checkout scm
    sh 'mvn -U clean compile'

    stage 'Verify'
    sh 'mvn verify'

    stage 'Quality'
    sh 'mvn -Dmaven.test.failure.ignore=false -P sonar sonar:sonar'

    stage 'Publish'
    sh 'mvn -T 4 deploy -Dmaven.install.skip=true'
    notifyBuild "SUCCESS"
  }
  catch (e) {
    notifyBuild "FAILED"
    throw e
  }
  
}
