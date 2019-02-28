package de.digitalcollections.commons.file.business.impl;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.commons.file.business.api.FileResourceService;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import org.w3c.dom.Document;

public abstract class FileResourceServiceImpl implements FileResourceService {

  protected FileResourceRepository repository;

  @Override
  public void assertReadability(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
    repository.assertReadability(resource);
  }

  @Override
  public FileResource create() {
    return repository.create();
  }

  @Override
  public void delete(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
    repository.delete(resource);
  }

  @Override
  public FileResource find(String identifier, MimeType mimeType) throws ResourceIOException, ResourceNotFoundException {
    return repository.find(identifier, mimeType);
  }

  @Override
  public byte[] getBytes(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
    return repository.getBytes(resource);
  }

  @Override
  public Document getDocument(FileResource fileResource) throws ResourceIOException, ResourceNotFoundException {
    return repository.getDocument(fileResource);
  }

  @Override
  public InputStream getInputStream(FileResource fileResource) throws ResourceIOException, ResourceNotFoundException {
    return repository.getInputStream(fileResource);
  }

  @Override
  public InputStream getInputStream(URI resourceUri) throws ResourceIOException, ResourceNotFoundException {
    return repository.getInputStream(resourceUri);
  }

  @Override
  public Reader getReader(FileResource resource) throws ResourceIOException, ResourceNotFoundException {
    return repository.getReader(resource);
  }

  @Override
  public long write(FileResource fileResource, String input) throws ResourceIOException {
    return repository.write(fileResource, input);
  }

  @Override
  public long write(FileResource fileResource, InputStream inputStream) throws ResourceIOException {
    return repository.write(fileResource, inputStream);
  }
}
