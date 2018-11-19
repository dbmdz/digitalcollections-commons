package de.digitalcollections.commons.file.business.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import org.w3c.dom.Document;

public interface FileResourceService {

  FileResource create(String key, FileResourcePersistenceType fileResourcePersistenceType, MimeType mimeType) throws ResourceIOException;

  FileResource create(MimeType mimeType) throws ResourceIOException;

  default FileResource create(String key, FileResourcePersistenceType fileResourcePersistenceType, String fileExtension) throws ResourceIOException {
    return create(key, fileResourcePersistenceType, MimeType.fromExtension(fileExtension));
  }
  
  default FileResource createManaged(MimeType mimeType) throws ResourceIOException {
    return create(null, FileResourcePersistenceType.MANAGED, mimeType);
  }

  FileResource get(String key, FileResourcePersistenceType fileResourcePersistenceType, MimeType mimeType) throws ResourceIOException;

  default FileResource get(String key, FileResourcePersistenceType fileResourcePersistenceType, String fileExtension) throws ResourceIOException {
    return get(key, fileResourcePersistenceType, MimeType.fromExtension(fileExtension));
  }

  Document getDocument(FileResource fileResource) throws ResourceIOException;

  default Document getDocument(String key, FileResourcePersistenceType fileResourcePersistenceType) throws ResourceIOException {
    FileResource fileResource = get(key, fileResourcePersistenceType, MimeType.fromExtension("xml"));
    return getDocument(fileResource);
  }

  void assertReadability(FileResource resource) throws ResourceIOException;

  default void assertReadability(String key, FileResourcePersistenceType resourcePersistenceType, String fileExtension) throws ResourceIOException {
    try {
      FileResource resource = get(key, resourcePersistenceType, MimeType.fromExtension(fileExtension));
      assertReadability(resource);
    } catch (ResourceIOException e) {
      throw e;
    } catch (Exception e) {
      throw new ResourceIOException(e.getMessage());
    }
  }


  InputStream getInputStream(FileResource fileResource) throws ResourceIOException;

  InputStream getInputStream(URI resourceUri) throws ResourceIOException;

  long write(FileResource fileResource, String input) throws ResourceIOException;

  long write(FileResource fileResource, InputStream inputStream) throws ResourceIOException;

  Set<String> findKeys(String keyPattern, FileResourcePersistenceType fileResourcePersistenceType) throws ResourceIOException;
}
