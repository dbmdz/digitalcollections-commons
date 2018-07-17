package de.digitalcollections.commons.file.backend.impl.handler;

import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CustomResourcePersistenceTypeHandler implements ResourcePersistenceTypeHandler {

  @Override
  public FileResourcePersistenceType getResourcePersistenceType() {
    return FileResourcePersistenceType.CUSTOM;
  }

  @Override
  public List<URI> getUris(String resolvingKey, MimeType mimeType) throws ResourceIOException {
    return Collections.singletonList(URI.create(resolvingKey));
  }
}
