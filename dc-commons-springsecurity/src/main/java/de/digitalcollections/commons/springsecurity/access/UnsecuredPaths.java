package de.digitalcollections.commons.springsecurity.access;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableConfigurationProperties(UnsecuredPathsConfig.class)
public class UnsecuredPaths {

  private static final Logger LOGGER = LoggerFactory.getLogger(UnsecuredPaths.class);

  private final UnsecuredPathsConfig unsecuredPathsConfig;

  @Autowired
  public UnsecuredPaths(UnsecuredPathsConfig unsecuredPathsConfig) {
    this.unsecuredPathsConfig = unsecuredPathsConfig;
  }

  private List<String> unsecuredPaths = new ArrayList<>();

  @PostConstruct()
  void init() {

    if (unsecuredPathsConfig.getUnsecured() != null) {
      unsecuredPaths = unsecuredPathsConfig.getUnsecured();
    } else {
      unsecuredPaths.addAll(
          Arrays.asList(
              "/health",
              "/info",
              "/javamelody",
              "/jolokia",
              "/jolokia/**",
              "/jsondoc",
              "/monitoring**",
              "/monitoring/health",
              "/monitoring/jolokia",
              "/monitoring/jolokia/**",
              "/monitoring/prometheus",
              "/monitoring/prometheus/**",
              "/monitoring/version",
              "/resources/**",
              "/version"));
    }

    LOGGER.info("Unsecured paths=" + unsecuredPaths);
  }

  public List<String> getUnsecuredPaths() {
    return unsecuredPaths;
  }

  public void clearUnsecuredPaths() {
    unsecuredPaths.clear();
  }

  public void addUnsecuredPath(String pathToAdd) {
    this.unsecuredPaths.add(pathToAdd);
  }
}
