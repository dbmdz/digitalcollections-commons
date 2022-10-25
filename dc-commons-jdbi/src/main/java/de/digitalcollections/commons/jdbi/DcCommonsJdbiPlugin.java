package de.digitalcollections.commons.jdbi;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.spi.JdbiPlugin;

public class DcCommonsJdbiPlugin implements JdbiPlugin {

  @Override
  public void customizeJdbi(Jdbi db) {
    db.registerArgument(new LocaleArgumentFactory());
    db.registerColumnMapper(new LocaleColumnMapperFactory());
    db.registerArgument(new MimeTypeArgumentFactory());
    db.registerColumnMapper(new MimeTypeColumnMapperFactory());
    db.registerArgument(new UrlArgumentFactory());
    db.registerColumnMapper(new UrlColumnMapperFactory());

    // Register Mappings for Set<Locale>
    final LocaleSetMapper localeSetMapper = new LocaleSetMapper();
    System.out.println("Registering LocaleSetMapper");
    db.registerArgument(localeSetMapper);
    db.registerColumnMapper(localeSetMapper);
  }
}
