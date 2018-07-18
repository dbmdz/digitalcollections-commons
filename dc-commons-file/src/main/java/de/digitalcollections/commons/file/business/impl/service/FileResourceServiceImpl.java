package de.digitalcollections.commons.file.business.impl.service;

import de.digitalcollections.commons.file.backend.api.FileResourceRepository;
import java.io.InputStream;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import de.digitalcollections.commons.file.business.api.FileResourceService;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;

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
  public InputStream getInputStream(FileResource fileResource) throws ResourceIOException {
    return fileResourceRepository.getInputStream(fileResource);
  }

  @Override
  public InputStream getInputStream(URI resourceUri) throws ResourceIOException {
    return fileResourceRepository.getInputStream(resourceUri);
  }

  @Override
  public void write(FileResource fileResource, String input) throws ResourceIOException {
    fileResourceRepository.write(fileResource, input);
  }
}
