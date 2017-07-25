package de.digitalcollections.commons.yaml.joda;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class JodaTimeConstructor extends Constructor {

  public JodaTimeConstructor() {
    this.yamlConstructors.put(Tag.TIMESTAMP, new ConstructJodaTimestamp());
    this.yamlConstructors.put(new Tag("!localTimestamp"), new ConstructJodaLocalTimestamp());
  }

  private class ConstructJodaTimestamp extends ConstructYamlTimestamp {
    @Override
    public Object construct(Node node) {
      return new DateTime(super.construct(node));
    }
  }

  private class ConstructJodaLocalTimestamp extends ConstructYamlTimestamp {
    @Override
    public Object construct(Node node) {
      ScalarNode scalarNode = (ScalarNode) node;
      return LocalDateTime.parse(scalarNode.getValue());
    }
  }

}
