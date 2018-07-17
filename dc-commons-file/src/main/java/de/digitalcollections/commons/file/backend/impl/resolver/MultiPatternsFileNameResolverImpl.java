package de.digitalcollections.commons.file.backend.impl.resolver;

import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Component
public class MultiPatternsFileNameResolverImpl implements FileNameResolver, InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiPatternsFileNameResolverImpl.class);

  @Value(value = "${multiPatternResolvingFile:}")
  private String multiPatternResolvingFile;

  @Autowired
  ResourceLoader resourceLoader;

  private List<PatternFileNameResolverImpl> patternFileNameResolvers = new ArrayList<>();

  public MultiPatternsFileNameResolverImpl(List<PatternFileNameResolverImpl> patternFileNameResolvers) {
    this.patternFileNameResolvers = patternFileNameResolvers;
  }

  public MultiPatternsFileNameResolverImpl() {
  }

  public void addPattern(String regex, String replacement) {
    PatternFileNameResolverImpl resolver = new PatternFileNameResolverImpl(regex, replacement);
    patternFileNameResolvers.add(resolver);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    String filepath = getMultiPatternResolvingFile();
    if (StringUtils.isEmpty(filepath)) {
      return;
    }
    Resource patRes = getResource(filepath);

    if (patRes.exists() && patRes.isReadable()) {
      Constructor constructor = new Constructor(PatternFileNameResolverImpl[].class);
      Yaml yaml = new Yaml(constructor);
      this.patternFileNameResolvers = Arrays.asList((PatternFileNameResolverImpl[]) yaml.load(patRes.getInputStream()));
    }
  }

  private Resource getResource(String uriPath) throws ResourceIOException {
    if (uriPath.startsWith("/")) {
      uriPath = "file:" + uriPath;
    }
    URI resourceUri = URI.create(uriPath);
    String location = resourceUri.toString();
    LOGGER.info("Getting inputstream for location '{}'.", location);
    return resourceLoader.getResource(location);
  }

  public String getMultiPatternResolvingFile() {
    return multiPatternResolvingFile;
  }

  public void setMultiPatternResolvingFile(String multiPatternResolvingFile) throws Exception {
    this.multiPatternResolvingFile = multiPatternResolvingFile;
    afterPropertiesSet();
  }

  @Override
  public List<String> getStrings(String identifier) throws ResourceIOException {
    return patternFileNameResolvers.stream()
            .filter(r -> r.isResolvable(identifier))
            .map(r -> r.getStrings(identifier))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
  }

  @Override
  public Boolean isResolvable(String identifier) {
    return patternFileNameResolvers.stream()
            .filter(r -> r.isResolvable(identifier))
            .findFirst().isPresent();
  }
}
