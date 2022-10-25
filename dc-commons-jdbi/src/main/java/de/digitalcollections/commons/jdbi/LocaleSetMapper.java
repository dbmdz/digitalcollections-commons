package de.digitalcollections.commons.jdbi;

import java.lang.reflect.Type;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class LocaleSetMapper implements ArgumentFactory, ColumnMapper<Set<Locale>> {

  @Override
  public Optional<Argument> build(Type type, Object value, ConfigRegistry config) {
    if (!(value instanceof Set)) {
      return Optional.empty();
    }

    Set<Object> unspecifiedSet = (Set<Object>) value;
    if (unspecifiedSet.isEmpty()) {
      return Optional.of(
          (position, statement, ctx) -> {
            statement.setString(position, String.format("{}"));
          });
    }

    if (!(unspecifiedSet.stream().findFirst().get() instanceof Locale)) {
      return Optional.empty();
    }

    return Optional.of(
        (position, statement, ctx) -> {
          statement.setString(
              position,
              String.format(
                  "{%s}",
                  unspecifiedSet.stream()
                      .map(l -> "" + l.toString() + "")
                      .collect(Collectors.joining(","))));
        });
  }

  @Override
  public Set<Locale> map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
    Array localeArray = r.getArray(columnNumber);
    if (localeArray == null) {
      return null;
    }
    String[] locales = (String[]) localeArray.getArray();
    return Arrays.stream(locales).map(Locale::forLanguageTag).collect(Collectors.toSet());
  }

  @Override
  public Set<Locale> map(ResultSet r, String columnLabel, StatementContext ctx)
      throws SQLException {
    Array localeArray = r.getArray(columnLabel);
    if (localeArray == null) {
      return null;
    }
    String[] locales = (String[]) localeArray.getArray();
    return Arrays.stream(locales).map(Locale::forLanguageTag).collect(Collectors.toSet());
  }

  @Override
  public void init(ConfigRegistry registry) {
    ColumnMapper.super.init(registry);
  }
}
