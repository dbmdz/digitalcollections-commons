stage 'Build'
node {
	checkout scm
  env.JAVA_HOME = tool 'jdk-8-oracle'
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
	sh 'mvn clean install'
}

stage 'Verify'
node {
  env.JAVA_HOME = tool 'jdk-8-oracle'
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
	sh 'mvn verify'
}

stage 'Quality'
node {
  env.JAVA_HOME = tool 'jdk-8-oracle'
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
  sh 'mvn -Dmaven.test.failure.ignore=false -P sonar sonar:sonar'
}
