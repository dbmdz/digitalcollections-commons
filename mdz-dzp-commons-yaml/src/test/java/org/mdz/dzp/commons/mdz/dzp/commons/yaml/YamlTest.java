package org.mdz.dzp.commons.mdz.dzp.commons.yaml;

import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mdz.dzp.commons.mdz.dzp.commons.yaml.joda.JodaTimeConstructor;
import org.mdz.dzp.commons.mdz.dzp.commons.yaml.joda.JodaTimeRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

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
  public void shouldFailToTestJenkins() {
    Assertions.fail("Break test to test Jenkins.");
  }

}
