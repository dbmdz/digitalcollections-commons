package de.digitalcollections.commons.springsecurity.access;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( prefix = "security.access" )
public class UnsecuredPathsConfig {

  private List<String> unsecured;

  public void setUnsecured(List<String> unsecured) {
    this.unsecured = unsecured;
  }

  public List<String> getUnsecured() {
    return this.unsecured;
  }
}
