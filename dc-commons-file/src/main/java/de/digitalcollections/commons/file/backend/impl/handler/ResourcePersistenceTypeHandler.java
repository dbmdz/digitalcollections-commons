package de.digitalcollections.commons.file.backend.impl.handler;

import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.net.URI;
import java.util.List;

public interface ResourcePersistenceTypeHandler {

  FileResourcePersistenceType getResourcePersistenceType();

  List<URI> getUris(String resolvingKey, MimeType mimeType) throws ResourceIOException;

  default URI getUri(String resolvingKey, String fileExtension) throws ResourceIOException {
    List<URI> uris = getUris(resolvingKey, fileExtension);
    if (uris.isEmpty()) {
      throw new ResourceIOException("Could not find URI for " + resolvingKey + " with extension " + fileExtension);
    } else {
      return uris.get(0);
    }
  }

  default List<URI> getUris(String resolvingKey, String filenameExtension) throws ResourceIOException {
    return getUris(resolvingKey, MimeType.fromExtension(filenameExtension));
  };
}
