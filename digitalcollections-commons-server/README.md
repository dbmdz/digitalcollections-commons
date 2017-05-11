# DigitalCollections Commons Server

Utilities for Server Services (Servlets).

## Usage

pom.xml:

```xml
<dependency>
  <groupId>de.digitalcollections.commons</groupId>
  <artifactId>digitalcollections-commons-server</artifactId>
  <version>1.2.2-SNAPSHOT</version>
</dependency>
```

## LogSessionIdFilter

A servlet filter for adding the current session id of the request to the MDC logging context.

### Configuration

Add the filter to the webapp's filter chain.

Example in a Spring MVC webapp with WebappInitializer:

```java
public class WebappInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
  ...

  @Override
  protected Filter[] getServletFilters() {
    // session id for logging, see log4j.xml
    final LogSessionIdFilter logSessionIdFilter = new LogSessionIdFilter();
    return new Filter[]{logSessionIdFilter};
  }
}
```

### Usage

Usage in log4j.xml:

```xml
<appender name="console" class="org.apache.log4j.ConsoleAppender">
  <param name="Target" value="System.out"/>
  <layout class="org.apache.log4j.PatternLayout">
    <!-- use LogSessionIdFilter and show only the first 5 characters of session id -->
    <param name="ConversionPattern" value="[%d{ISO8601} %-5p] [%.5X{sessionID}...] %-25c{1} (%-8t) > %m%n"/>
  </layout>
</appender>
```

## HttpLoggingUtilities

Currently this provides a utility class for logging from HTTP Services via Logstash Markers,
for logging things like client IP, geographical location, referers and other useful information
from HTTP Requests.

This product includes GeoLite2 data created by MaxMind, available from
[http://www.maxmind.com](http://www.maxmind.com).

