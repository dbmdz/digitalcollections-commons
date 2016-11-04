package org.mdz.dzp.commons.yaml;

import static org.assertj.core.api.Assertions.assertThat;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import static org.mdz.dzp.commons.yaml.StringRepresentations.fromStringRepresetation;
import static org.mdz.dzp.commons.yaml.StringRepresentations.stringRepresentationOf;
import org.mdz.dzp.commons.yaml.examples.Person;

public class StringRepresetationsTest {

  private Person boris;

  @Before
  public void setUp() {
    boris = new Person("Boris", "Strugatzki", DateTime.parse("1933-04-15"));
  }

  @Test
  public void stringRepresetationShouldNotEndWithNewline() {
    assertThat(StringRepresentations.stringRepresentationOf(boris)).doesNotEndWith("\n");
  }

  @Test
  public void shouldSerializeAndDeserialize() {
    DateTime dateTime = DateTime.now();
    assertThat(fromStringRepresetation(stringRepresentationOf(boris))).isEqualToComparingFieldByField(boris);
  }

}
