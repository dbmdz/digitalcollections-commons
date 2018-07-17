package de.digitalcollections.commons.file.backend.impl.resolver;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternFileNameResolverImpl implements FileNameResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(PatternFileNameResolverImpl.class);

  private Pattern compiledPattern;

  private String pattern;

  private List<String> substitutions;

  public PatternFileNameResolverImpl() {
  }

  public PatternFileNameResolverImpl(String regex, String replacement) {
    this.pattern = regex;
    this.compiledPattern = Pattern.compile(regex);
    this.substitutions = Collections.singletonList(replacement.replace("~", System.getProperty("user.home")));
  }

  public String getPattern() {
    return pattern;
  }

  public List<String> getSubstitutions() {
    return substitutions;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
    this.compiledPattern = Pattern.compile(pattern);
  }

  public void setSubstitutions(List<String> substitutions) {
    this.substitutions = substitutions.stream()
            .map(s -> s.replace("~", System.getProperty("user.home")))
            .collect(Collectors.toList());
  }

  @Override
  public List<String> getStrings(String identifier) {
    Matcher matcher = this.compiledPattern.matcher(identifier);
    return this.substitutions.stream()
            .map(matcher::replaceAll)
            .collect(Collectors.toList());
  }

  @Override
  public Boolean isResolvable(String identifier) {
    Boolean b = this.compiledPattern.matcher(identifier).matches();
    LOGGER.debug("Matching " + identifier + " against " + this.pattern + " is " + b);
    return b;
  }

  /*
  @Override
  public String toString() {
    return StringRepresentations.stringRepresentationOf(this);
  }
   */
}
