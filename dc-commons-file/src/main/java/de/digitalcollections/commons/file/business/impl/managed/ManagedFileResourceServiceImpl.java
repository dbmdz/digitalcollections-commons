package de.digitalcollections.commons.file.business.impl.managed;

import de.digitalcollections.commons.file.backend.impl.managed.ManagedFileResourceRepositoryImpl;
import de.digitalcollections.commons.file.business.api.FileResourceService;
import de.digitalcollections.commons.file.business.impl.FileResourceServiceImpl;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagedFileResourceServiceImpl extends FileResourceServiceImpl implements FileResourceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ManagedFileResourceServiceImpl.class);

  @Autowired
  public ManagedFileResourceServiceImpl(ManagedFileResourceRepositoryImpl repository) {
    this.repository = repository;
  }

  private ManagedFileResourceRepositoryImpl getRepo() {
    return (ManagedFileResourceRepositoryImpl) repository;
  }

  public FileResource create(String filename) {
    return getRepo().create(filename);
  }

  public FileResource create(MimeType mimeType, String filename) {
    return getRepo().create(mimeType, filename);
  }

  public FileResource create(UUID uuid) {
    return getRepo().create(uuid);
  }

  public FileResource find(String uuid) throws ResourceIOException, ResourceNotFoundException {
    return getRepo().find(uuid);
  }
}
