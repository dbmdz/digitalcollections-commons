package de.digitalcollections.commons.file.backend.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import org.w3c.dom.Document;

public interface FileResourceRepository<R extends FileResource> {

  FileResource create(MimeType mimeType) throws ResourceIOException;

  FileResource create(String key, FileResourcePersistenceType resourcePersistenceType, MimeType mimeType) throws ResourceIOException;

  default FileResource create(String key, FileResourcePersistenceType resourcePersistenceType, String filenameExtension) throws ResourceIOException {
    return create(key, resourcePersistenceType, MimeType.fromExtension(filenameExtension));
  }

  void delete(R resource) throws ResourceIOException;

  FileResource find(String key, FileResourcePersistenceType resourcePersistenceType, MimeType mimeType) throws ResourceIOException;

  default FileResource find(String key, FileResourcePersistenceType resourcePersistenceType, String filenameExtension) throws ResourceIOException {
    return find(key, resourcePersistenceType, MimeType.fromExtension(filenameExtension));
  }

  byte[] getBytes(R resource) throws ResourceIOException;

  Document getDocument(R resource) throws ResourceIOException;

  default Document getDocument(String key, FileResourcePersistenceType resourcePersistenceType) throws ResourceIOException {
    FileResource resource = find(key, resourcePersistenceType, MimeType.fromExtension("xml"));
    return getDocument((R) resource);
  }

  InputStream getInputStream(URI resourceUri) throws ResourceIOException;

  InputStream getInputStream(R resource) throws ResourceIOException;

  Reader getReader(R resource) throws ResourceIOException;

  void write(FileResource resource, String input) throws ResourceIOException;

  void write(FileResource resource, InputStream inputStream) throws ResourceIOException;

}
