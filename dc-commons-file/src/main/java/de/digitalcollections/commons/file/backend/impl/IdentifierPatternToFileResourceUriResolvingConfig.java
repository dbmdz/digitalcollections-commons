package de.digitalcollections.commons.file.backend.impl;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("resource-repository.resolved")
public class IdentifierPatternToFileResourceUriResolvingConfig {

  private List<IdentifierPatternToFileResourceUriResolverImpl> patterns;

  public List<IdentifierPatternToFileResourceUriResolverImpl> getPatterns() {
    return this.patterns;
  }

  // IMPORTANT: patterns does not get filled without setter! (not mentioned in Spring Boot doc!)
  public void setPatterns(List<IdentifierPatternToFileResourceUriResolverImpl> patterns) {
    this.patterns = patterns;
  }
}
