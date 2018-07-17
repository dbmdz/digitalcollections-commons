package de.digitalcollections.commons.file.backend.impl.handler;

import de.digitalcollections.commons.file.backend.impl.resolver.FileNameResolver;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResolvedResourcePersistenceTypeHandler implements ResourcePersistenceTypeHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolvedResourcePersistenceTypeHandler.class);

  @Autowired
  private List<FileNameResolver> fileNameResolvers;

  @Override
  public FileResourcePersistenceType getResourcePersistenceType() {
    return FileResourcePersistenceType.RESOLVED;
  }

  @Override
  public List<URI> getUris(String resolvingKey, MimeType mimeType) throws ResourceIOException {
    FileNameResolver fileNameResolver = getFileNameResolver(resolvingKey);
    return fileNameResolver.getUris(resolvingKey, mimeType);
  }

  private FileNameResolver getFileNameResolver(String key) throws ResourceIOException {
    return fileNameResolvers.stream()
            .filter(r -> r.isResolvable(key))
            .findFirst()
            .orElseThrow(() -> new ResourceIOException(key + " not resolvable!"));
  }
}
