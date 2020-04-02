package de.digitalcollections.commons.yaml.joda;

import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class JodaTimeRepresenter extends Representer {

  public JodaTimeRepresenter() {
    multiRepresenters.put(
        DateTime.class,
        data -> {
          DateTime date = (DateTime) data;
          return super.representData(new Date(date.getMillis()));
        });
    multiRepresenters.put(
        LocalDateTime.class,
        data -> {
          LocalDateTime date = (LocalDateTime) data;
          return super.representScalar(new Tag("!localTimestamp"), date.toString());
        });
  }
}
