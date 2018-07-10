# Digital Collections Commons Spring Boot

Offer common used endpoints and contributors for Spring Boot 2 applications as well as monitoring services.

## Usage

Add the following artifact to your maven ```pom.xml```

```xml
<dependency>
   <groupId>de.digitalcollections.commons</groupId>
   <artifactId>dc-commons-springboot</artifactId>
   <version>1.4.0</version>
</dependency>
```

## Acturator endpoints

### ```version``` (```VersionActuatorEndpoint```)

The ```version``` actuator endpoint offers detailed information about the current artifact. It depends
on the ```VersionInfo``` bean in the ```monitoring``` package.

The output format of this endpoint is JSON, like in the following example:

```json
{"name":"Chuck Norris Meme Generator","version":"1.2.3"}
```

To use this endpoint, add the following two packages to your Spring Component scan:

```java
@ComponentScan(basePackages = {
    "de.digitalcollections.commons.springboot.actuator",
    "de.digitalcollections.commons.springboot.monitoring"
})
``` 

You also have to enable the endpoint in your application configuration, e.g. ```application.yml```:

```yml
management:
  endpoints:
    web:
      exposure:
        include:
          - version
```

## Beans

### ```VersionInfoBean```

This bean reads the build information from the ```application.yml```, which is set by the maven build process and
also automatically collects all version information from all dependencies (jar files).

For this, you have to set the following part in your ```application.yml```:

```yml
info:
  app:
    project:
      name: '@project.name@'
      groupId: @project.groupId@
      artifactId: @project.artifactId@
      version: @project.version@ 
      buildDetails: '@versionName@'  
```

To fill these values, you need the following parts in your ```pom.xml```:

```xml
[...]
<properties>
  <timestamp>${maven.build.timestamp}</timestamp>
  <maven.build.timestamp.format>yyyy-MM-dd HH:mm:ss</maven.build.timestamp.format>
  <versionName>${project.version} manually built by ${user.name} at ${maven.build.timestamp}</versionName>
</properties>
[...]
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-resources-plugin</artifactId>
      <version>2.7</version>
      <configuration>
        <delimiters>
          <delimiter>@</delimiter>
        </delimiters>
        <useDefaultDelimiters>false</useDefaultDelimiters>
      </configuration>
    </plugin>
  </plugins>
</build>
[...]
<resources>
  <resource>
    <directory>src/main/resources</directory>
    <filtering>true</filtering><!-- Process "@" placeholders -->
    <includes>
      <include>**/application.yml</include>
    </includes>
  </resource>
  <resource>
    <directory>src/main/resources</directory>
    <filtering>false</filtering><!-- This time no more "@" -->
    <excludes>
      <exclude>**/application.yml</exclude>
    </excludes>
  </resource>
</resources>
[...]
```

If you use GitLabCI, you can use an even better approach for setting the build details:

```xml
[...]
<profiles>
  <profile>
    <!-- Profile that extends the printable version number by an optional build.
    It is activated when an environment variable called CI_PIPELINE_ID exists (as in Gitlab CI) -->
    <id>versionNameBuildNumber</id>
    <activation>
      <property>
        <name>env.CI_PIPELINE_ID</name>
      </property>
    </activation>
    <properties>
      <versionName>Version: ${project.version}, Branch: ${env.CI_COMMIT_REF_NAME}, Commit SHA: ${env.CI_COMMIT_SHA}, Pipeline #${env.CI_PIPELINE_ID} on ${env.CI_RUNNER_DESCRIPTION} triggered by ${env.CI_PIPELINE_SOURCE} from ${env.GITLAB_USER_LOGIN} at ${maven.build.timestamp} UTC</versionName>
    </properties>
  </profile>
</profiles>
[...]
```

## Contributors

### ```VersionInfoContributor```

The ```VersionInfoContributor``` adds all version information from the ```VersionInfo``` bean to the
```info``` actuator endpoint.

To enable it, extend your Spring Component scan with the following package:

```java
@ComponentScan(basePackages = {
    "de.digitalcollections.commons.springboot.contributor"
})
```

## Services

### ```MetricsService```

This service offers a lightweight API to *push* data into the ```io.micrometer``` monitoring, which uses the publish-subscribe pattern.

To use it, extend your Spring Component scan with the following package:

```java
@ComponentScan(basePackages = {
    "de.digitalcollections.commons.springboot.metrics"
})

[...]

@Autowired MetricsService metricsService;
```

Its API is pretty straightforward, ranging from a simple

```java
setGauge(String name, long value);
```

method to set the value of a gauge (postfixed with ```.amount``), up to complex methods like

```java
increaseCounterWithDurationAndPercentiles(String name, String tag, Long durationMillis);
```

to simultaneously increase a gauge value with a tag and log the duration, e.g. of a previous function call.