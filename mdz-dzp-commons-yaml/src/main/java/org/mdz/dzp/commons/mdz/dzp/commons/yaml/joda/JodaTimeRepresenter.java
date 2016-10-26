package org.mdz.dzp.commons.mdz.dzp.commons.yaml.joda;

import java.util.Date;
import org.joda.time.DateTime;
import org.yaml.snakeyaml.representer.Representer;

public class JodaTimeRepresenter extends Representer {

  public JodaTimeRepresenter() {
    multiRepresenters.put(DateTime.class, data -> {
      DateTime date = (DateTime) data;
      return super.representData(new Date(date.getMillis()));
    });
  }

}
