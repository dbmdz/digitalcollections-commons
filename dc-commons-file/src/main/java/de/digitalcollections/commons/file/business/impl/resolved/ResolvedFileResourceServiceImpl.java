package de.digitalcollections.commons.file.business.impl.resolved;

import de.digitalcollections.commons.file.backend.impl.resolved.ResolvedFileResourceRepositoryImpl;
import de.digitalcollections.commons.file.business.api.FileResourceService;
import de.digitalcollections.commons.file.business.impl.FileResourceServiceImpl;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResolvedFileResourceServiceImpl extends FileResourceServiceImpl implements FileResourceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolvedFileResourceServiceImpl.class);

  @Autowired
  public ResolvedFileResourceServiceImpl(ResolvedFileResourceRepositoryImpl repository) {
    this.repository = repository;
  }

  private ResolvedFileResourceRepositoryImpl getRepo() {
    return (ResolvedFileResourceRepositoryImpl) repository;
  }

  public FileResource create(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException {
    return getRepo().create(identifier, mimeType, readOnly);
  }

  public FileResource create(String identifier, String filenameExtension, boolean readOnly) throws ResourceIOException {
    return getRepo().create(identifier, filenameExtension, readOnly);
  }

  public FileResource find(String identifier, MimeType mimeType, boolean readOnly) throws ResourceIOException, ResourceNotFoundException {
    return getRepo().find(identifier, mimeType, readOnly);
  }

  public FileResource find(String identifier, String filenameExtension, boolean readOnly) throws ResourceIOException, ResourceNotFoundException {
    return getRepo().find(identifier, filenameExtension, readOnly);
  }

  public Set<String> findKeys(String keyPattern) throws ResourceIOException {
    return getRepo().findKeys(keyPattern);
  }

  public Set<Path> getPathsByPattern(String pattern) throws ResourceIOException {
    return getRepo().getPathsByPattern(pattern);
  }

  public List<URI> getUris(String identifier, MimeType mimeType) throws ResourceIOException {
    return getRepo().getUris(identifier, mimeType);
  }

  public List<String> getUrisAsString(String identifier) throws ResourceIOException {
    return getRepo().getUrisAsString(identifier);
  }
}
