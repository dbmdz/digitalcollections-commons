package de.digitalcollections.commons.yaml;

import de.digitalcollections.commons.yaml.joda.JodaTimeConstructor;
import de.digitalcollections.commons.yaml.joda.JodaTimeRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class StringRepresentations {

  private static final Yaml YAML;

  static {
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
    options.setSplitLines(false);
    options.setLineBreak(DumperOptions.LineBreak.UNIX);
    YAML = new Yaml(new JodaTimeConstructor(), new JodaTimeRepresenter(), options);
  }

  /**
   * Convert the given object to a string representation using YAML.
   * 
   * @param object The object to make a string of.
   * @return A YAML string representing the object in its current state.
   */
  public static String stringRepresentationOf(Object object) {
    String string = YAML.dump(object);
    return string.substring(0, string.length() - 1);
  }

  public static Object fromStringRepresetation(String string) {
    return YAML.load(string);
  }

  public static <T> T fromStringRepresetation(Class<T> type, String string) {
    return YAML.loadAs(string, type);
  }

}
