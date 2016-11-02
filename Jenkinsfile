node {
	env.JAVA_HOME = tool 'jdk-8-oracle'
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"

  notifyBuild "STARTED"
  
  try {
    stage('Build') {
      checkout scm
      sh 'mvn -U -B clean compile'
    }

    stage('Verify') {
      sh 'mvn -B verify'
    }
    
    stage('Quality') {
      sh 'mvn -B -Dmaven.test.failure.ignore=false -P sonar sonar:sonar'
    }

    stage('Publish') {
      sh 'mvn -B -T 4 deploy -Dmaven.install.skip=true'
    }
    
    notifyBuild "SUCCESS"
  }
  catch (e) {
    notifyBuild "FAILED"
    throw e
  }
  
}
