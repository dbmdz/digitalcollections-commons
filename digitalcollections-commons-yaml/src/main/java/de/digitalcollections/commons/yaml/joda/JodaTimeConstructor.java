package de.digitalcollections.commons.yaml.joda;

import org.joda.time.DateTime;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

public class JodaTimeConstructor extends Constructor {

  public JodaTimeConstructor() {
    this.yamlConstructors.put(Tag.TIMESTAMP, new ConstructJodaTimestamp());
  }

  private class ConstructJodaTimestamp extends ConstructYamlTimestamp {
    public Object construct(Node node) {
      return new DateTime(super.construct(node));
    }
  }

}
