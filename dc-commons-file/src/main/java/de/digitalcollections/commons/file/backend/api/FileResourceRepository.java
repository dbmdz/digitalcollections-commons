package de.digitalcollections.commons.file.backend.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Set;
import org.w3c.dom.Document;

public interface FileResourceRepository {

  FileResource create(MimeType mimeType) throws ResourceIOException;

  FileResource create(String key, FileResourcePersistenceType resourcePersistenceType, MimeType mimeType) throws ResourceIOException;

  default FileResource create(String key, FileResourcePersistenceType resourcePersistenceType, String filenameExtension) throws ResourceIOException {
    return create(key, resourcePersistenceType, MimeType.fromExtension(filenameExtension));
  }

  void delete(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  FileResource find(String key, FileResourcePersistenceType resourcePersistenceType, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException;

  default FileResource find(String key, FileResourcePersistenceType resourcePersistenceType, String filenameExtension) throws ResourceIOException, ResourceNotFoundException {
    return find(key, resourcePersistenceType, MimeType.fromExtension(filenameExtension));
  }

  byte[] getBytes(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  Document getDocument(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  default Document getDocument(String key, FileResourcePersistenceType resourcePersistenceType) throws ResourceIOException, ResourceNotFoundException {
    FileResource resource = find(key, resourcePersistenceType, MimeType.fromExtension("xml"));
    return getDocument(resource);
  }

  void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  long write(FileResource resource, String input) throws ResourceIOException;

  long write(FileResource resource, InputStream inputStream) throws ResourceIOException;

  Set<String> findKeys(String keyPattern, FileResourcePersistenceType resourcePersistenceType) throws ResourceIOException;
}
