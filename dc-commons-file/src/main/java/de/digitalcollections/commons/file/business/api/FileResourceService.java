package de.digitalcollections.commons.file.business.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import org.w3c.dom.Document;

public interface FileResourceService {

  FileResource createManaged(MimeType mimeType, String filename);

  default FileResource createManaged(String contentType, String filename) {
    return createManaged(MimeType.fromTypename(contentType), filename);
  }

  default FileResource createManaged(MimeType mimeType) throws ResourceIOException {
    return createManaged(mimeType, null);
  }

  FileResource createResolved(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException;

  default FileResource createResolved(MimeType mimeType) throws ResourceIOException {
    return createResolved(null, mimeType, true);
  }

  FileResource getManaged(UUID uuid) throws ResourceIOException, ResourceNotFoundException;

  FileResource getResolved(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException, ResourceNotFoundException;

  default FileResource getResolved(String identifier, String filenameExtension, boolean readOnly) throws ResourceIOException, ResourceNotFoundException {
    return getResolved(identifier, MimeType.fromExtension(filenameExtension), readOnly);
  }

  Document getDocument(FileResource fileResource) throws ResourceIOException, ResourceNotFoundException;

  void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(FileResource fileResource) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;

  long write(FileResource fileResource, String input) throws ResourceIOException;

  long write(FileResource fileResource, InputStream inputStream) throws ResourceIOException;

  Set<String> findKeysForResolvedFileResources(String keyPattern) throws ResourceIOException;
}
