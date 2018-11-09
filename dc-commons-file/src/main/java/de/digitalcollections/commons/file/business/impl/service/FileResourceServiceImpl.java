package de.digitalcollections.commons.file.business.impl.service;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import de.digitalcollections.commons.file.business.api.FileResourceService;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
public class FileResourceServiceImpl implements FileResourceService {

  @Autowired
  private FileResourceRepository fileResourceRepository;

  @Override
  public FileResource create(String key, FileResourcePersistenceType fileResourcePersistenceType, MimeType mimeType) throws ResourceIOException {
    return fileResourceRepository.create(key, fileResourcePersistenceType, mimeType);
  }

  @Override
  public FileResource get(String key, FileResourcePersistenceType fileResourcePersistenceType, MimeType mimeType) throws ResourceIOException {
    return fileResourceRepository.find(key, fileResourcePersistenceType, mimeType);
  }

  @Override
  public Document getDocument(FileResource fileResource) throws ResourceIOException {
    return fileResourceRepository.getDocument(fileResource);
  }

  @Override
  public void assertReadability(FileResource resource) throws ResourceIOException {
    fileResourceRepository.assertReadability(resource);
  }

  @Override
  public InputStream getInputStream(FileResource fileResource) throws ResourceIOException {
    return fileResourceRepository.getInputStream(fileResource);
  }

  @Override
  public InputStream getInputStream(URI resourceUri) throws ResourceIOException {
    return fileResourceRepository.getInputStream(resourceUri);
  }

  @Override
  public long write(FileResource fileResource, String input) throws ResourceIOException {
    return fileResourceRepository.write(fileResource, input);
  }

  @Override
  public long write(FileResource fileResource, InputStream inputStream) throws ResourceIOException {
    return fileResourceRepository.write(fileResource, inputStream);
  }

  @Override
  public FileResource create(MimeType mimeType) throws ResourceIOException {
    return fileResourceRepository.create(mimeType);
  }

  @Override
  public Set<String> findKeys(String keyPattern, FileResourcePersistenceType fileResourcePersistenceType) throws ResourceIOException {
    return fileResourceRepository.findKeys(keyPattern, fileResourcePersistenceType);
  }
}
