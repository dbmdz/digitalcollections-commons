package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.commons.file.backend.api.IdentifierToFileResourceUriResolver;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CustomIdentifierToFileResourceUriResolverImpl implements IdentifierToFileResourceUriResolver {

  @Override
  public List<String> getUrisAsStrings(String identifier) {
    return Arrays.asList("file://" + identifier + ".jpeg");
  }

  @Override
  public Boolean isResolvable(String identifier) {
    if (!identifier.contains(" ")) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

}
