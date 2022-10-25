package de.digitalcollections.commons.jdbi;

import java.sql.Types;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

public class LocaleSetArgumentFactory extends AbstractArgumentFactory<Set<Locale>> {

  public LocaleSetArgumentFactory() {
    super(Types.ARRAY);
  }

  @Override
  protected Argument build(Set<Locale> value, ConfigRegistry config) {
    if (value == null) {
      return null;
    }

    return (position, preparedStatement, statementContext) ->
        preparedStatement.setObject(
            position,
            String.format(
                "{%s}", value.stream().map(Locale::toString).collect(Collectors.joining(","))),
            Types.VARCHAR); // heuristically determined - an array is represented by a varchar
  }
}
