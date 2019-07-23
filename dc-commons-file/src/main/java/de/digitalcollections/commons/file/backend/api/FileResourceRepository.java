package de.digitalcollections.commons.file.backend.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

public interface FileResourceRepository {

  void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  FileResource create();

  FileResource create(String identifier, MimeType mimeType) throws ResourceIOException;

  FileResource createByMimeType(MimeType mimeType);

  FileResource find(String identifier, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException;
}
