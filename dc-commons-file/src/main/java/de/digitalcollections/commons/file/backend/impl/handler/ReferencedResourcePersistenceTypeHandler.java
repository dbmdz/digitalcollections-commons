package de.digitalcollections.commons.file.backend.impl.handler;

import de.digitalcollections.model.api.identifiable.resource.enums.FileResourcePersistenceType;
import org.springframework.stereotype.Component;

@Component
public class ReferencedResourcePersistenceTypeHandler extends ResolvedResourcePersistenceTypeHandler {

  @Override
  public FileResourcePersistenceType getResourcePersistenceType() {
    return FileResourcePersistenceType.REFERENCED;
  }
}
