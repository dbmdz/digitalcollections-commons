package de.digitalcollections.commons.yaml;

import de.digitalcollections.commons.yaml.joda.JodaTimeConstructor;
import de.digitalcollections.commons.yaml.joda.JodaTimeRepresenter;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import static org.assertj.core.api.Assertions.assertThat;

public class YamlTest {

  private Yaml yaml;

  @Before
  public void setUp() {
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
    options.setSplitLines(false);
    yaml = new Yaml(new JodaTimeConstructor(), new JodaTimeRepresenter(), options);
  }

  @Test
  public void shouldSerializeAndDeserializeDateTime() {
    DateTime dateTime = DateTime.now();
    assertThat(yaml.load(yaml.dump(dateTime))).isEqualTo(dateTime);
  }

  @Test
  public void shouldSerializeAndDeserializeLocalDateTime() {
    LocalDateTime dateTime = LocalDateTime.now();
    assertThat(yaml.load(yaml.dump(dateTime))).isEqualTo(dateTime);
  }

}
