package de.digitalcollections.commons.file.backend.impl.resolved;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("resource-repository.resolved")
public class ResolvedFileResourcesConfig {

  private List<PatternFileNameResolverImpl> patterns;

  public List<PatternFileNameResolverImpl> getPatterns() {
    return this.patterns;
  }

  // IMPORTANT: patterns does not get filled without setter! (not mentioned in Spring Boot doc!)
  public void setPatterns(List<PatternFileNameResolverImpl> patterns) {
    this.patterns = patterns;
  }
}
