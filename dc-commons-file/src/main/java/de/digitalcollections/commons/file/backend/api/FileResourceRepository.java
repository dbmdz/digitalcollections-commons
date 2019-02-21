package de.digitalcollections.commons.file.backend.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import org.w3c.dom.Document;

public interface FileResourceRepository {

  // new
  FileResource createManaged(MimeType mimeType, String filename);

  default FileResource createManaged(String filename) {
    return createManaged(MimeType.fromFilename(filename), filename);
  }

  FileResource findManaged(UUID uuid) throws ResourceIOException, ResourceNotFoundException;

  default FileResource findManaged(String uuid) throws ResourceIOException, ResourceNotFoundException {
    return findManaged(UUID.fromString(uuid));
  }

  FileResource createResolved(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException;

  default FileResource createResolved(String identifier, String filenameExtension, boolean readOnly) throws ResourceIOException {
    return createResolved(identifier, MimeType.fromExtension(filenameExtension), readOnly);
  }

  FileResource findResolved(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException, ResourceNotFoundException;

  default FileResource findResolved(String identifier, String filenameExtension, boolean readOnly) throws ResourceIOException, ResourceNotFoundException {
    return findResolved(identifier, MimeType.fromExtension(filenameExtension), readOnly);
  }

  // old
//  default FileResource create(String identifier, String filenameExtension) throws ResourceIOException {
//    return createResolved(identifier, MimeType.fromExtension(filenameExtension), true);
//  }
  void delete(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

//  FileResource find(String key, FileResourcePersistenceType resourcePersistenceType, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException;
//  default FileResource find(String key, FileResourcePersistenceType resourcePersistenceType, String filenameExtension) throws ResourceIOException, ResourceNotFoundException {
//    return find(key, resourcePersistenceType, MimeType.fromExtension(filenameExtension));
//  }
  byte[] getBytes(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  Document getDocument(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  long write(FileResource resource, String input) throws ResourceIOException;

  long write(FileResource resource, InputStream inputStream) throws ResourceIOException;

  Set<String> findKeysForResolvedFileResources(String keyPattern) throws ResourceIOException;
}
