node {
	env.JAVA_HOME = tool 'jdk-8-oracle'
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"

	stage 'Build'
	checkout scm
	sh 'mvn clean install'

	stage 'Verify'
	sh 'mvn verify'

	stage 'Quality'
  sh 'mvn -Dmaven.test.failure.ignore=false -P sonar sonar:sonar'

	stage 'Publish'
  sh 'mvn -T 6 deploy'
}
