package de.digitalcollections.commons.file.business.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import org.w3c.dom.Document;

public interface FileResourceService {

  // new
  default FileResource createManaged(String contentType, String filename) {
    return createManaged(MimeType.fromTypename(contentType), filename);
  }

  FileResource createManaged(MimeType mimeType, String filename);

  // old
//  default FileResource createManaged(MimeType mimeType) throws ResourceIOException {
//    return create(null, FileResourcePersistenceType.MANAGED, mimeType);
//  }
  FileResource create(String key, FileResourcePersistenceType fileResourcePersistenceType, MimeType mimeType) throws ResourceIOException;

  default FileResource create(MimeType mimeType) throws ResourceIOException {
    return create(null, null, mimeType);
  }

  default FileResource create(String key, FileResourcePersistenceType fileResourcePersistenceType, String fileExtension) throws ResourceIOException {
    return create(key, fileResourcePersistenceType, MimeType.fromExtension(fileExtension));
  }

  FileResource get(String key, FileResourcePersistenceType fileResourcePersistenceType, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException;

  default FileResource get(String key, FileResourcePersistenceType fileResourcePersistenceType, String fileExtension) throws ResourceIOException, ResourceNotFoundException {
    return get(key, fileResourcePersistenceType, MimeType.fromExtension(fileExtension));
  }

  Document getDocument(FileResource fileResource) throws ResourceIOException, ResourceNotFoundException;

  default Document getDocument(String key, FileResourcePersistenceType fileResourcePersistenceType) throws ResourceIOException, ResourceNotFoundException {
    FileResource fileResource = get(key, fileResourcePersistenceType, MimeType.fromExtension("xml"));
    return getDocument(fileResource);
  }

  void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  default void assertReadability(String key, FileResourcePersistenceType resourcePersistenceType, String fileExtension) throws ResourceIOException, ResourceNotFoundException {
    FileResource resource = get(key, resourcePersistenceType, MimeType.fromExtension(fileExtension));
    assertReadability(resource);
  }

  InputStream getInputStream(FileResource fileResource) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;

  long write(FileResource fileResource, String input) throws ResourceIOException;

  long write(FileResource fileResource, InputStream inputStream) throws ResourceIOException;

  Set<String> findKeys(String keyPattern, FileResourcePersistenceType fileResourcePersistenceType) throws ResourceIOException;
}
