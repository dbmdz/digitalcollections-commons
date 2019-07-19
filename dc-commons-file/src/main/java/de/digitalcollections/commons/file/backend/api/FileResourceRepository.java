package de.digitalcollections.commons.file.backend.api;

import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.InputStream;

public interface FileResourceRepository {

  void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

//  FileResource create();
  FileResource create(String identifier, MimeType mimeType) throws ResourceIOException;

  FileResource createByMimeType(MimeType mimeType);

  FileResource find(String identifier, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException;

//  void delete(FileResource resource) throws ResourceIOException, ResourceNotFoundException;
//
//  byte[] getBytes(FileResource resource) throws ResourceIOException, ResourceNotFoundException;
//
//  Document getDocument(FileResource resource) throws ResourceIOException, ResourceNotFoundException;
//
//
//  InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException;
  InputStream getInputStream(FileResource resource) throws ResourceIOException, ResourceNotFoundException;

//  Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException;
//  long write(FileResource resource, String input) throws ResourceIOException;
  long write(FileResource resource, InputStream inputStream) throws ResourceIOException;
}
