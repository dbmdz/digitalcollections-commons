package de.digitalcollections.commons.file.business.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import org.w3c.dom.Document;

public interface FileResourceService {

  FileResource create();
  
  FileResource createByMimetype(MimeType mimeType);

  FileResource find(String identifier, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException;

  default FileResource find(String identifier, String fileExtension) throws ResourceIOException, ResourceNotFoundException {
    return find(identifier, MimeType.fromExtension(fileExtension));
  }

  void delete(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  byte[] getBytes(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  Document getDocument(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;

  InputStream getInputStream(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

  long write(FileResource resource, String input) throws ResourceIOException;

  long write(FileResource resource, InputStream inputStream) throws ResourceIOException;
}
