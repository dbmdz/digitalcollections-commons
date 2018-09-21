package de.digitalcollections.commons.jdbi;

import de.digitalcollections.model.api.identifiable.resource.MimeType;
import java.lang.reflect.Type;
import java.util.Optional;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.ColumnMapperFactory;

public class MimeTypeColumnMapperFactory implements ColumnMapperFactory {

  @Override
  public Optional<ColumnMapper<?>> build(Type type, ConfigRegistry config) {
    if (type == MimeType.class) {
      return Optional.of((resultSet, columnNumber, statementContext) -> {
        return MimeType.fromTypename(resultSet.getString(columnNumber));
      });
    }
    return Optional.empty();
  }

}
