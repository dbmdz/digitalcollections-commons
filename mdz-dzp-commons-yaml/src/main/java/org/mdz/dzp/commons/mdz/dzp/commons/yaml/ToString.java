package org.mdz.dzp.commons.mdz.dzp.commons.yaml;

import org.mdz.dzp.commons.mdz.dzp.commons.yaml.joda.JodaTimeConstructor;
import org.mdz.dzp.commons.mdz.dzp.commons.yaml.joda.JodaTimeRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class ToString {

  private static final Yaml YAML;

  static {
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
    options.setSplitLines(false);
    YAML = new Yaml(new JodaTimeConstructor(), new JodaTimeRepresenter(), options);
  }

  public static String stringRepresentationOf(Object object) {
    return YAML.dump(object);
  }

  public static Object fromStringRepresetation(String string) {
    return YAML.load(string);
  }

}
