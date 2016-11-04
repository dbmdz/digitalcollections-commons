package de.digitalcollections.commons.yaml;

import de.digitalcollections.commons.yaml.StringRepresentations;
import static org.assertj.core.api.Assertions.assertThat;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import static de.digitalcollections.commons.yaml.StringRepresentations.fromStringRepresetation;
import static de.digitalcollections.commons.yaml.StringRepresentations.stringRepresentationOf;
import de.digitalcollections.commons.yaml.examples.Person;
import org.joda.time.LocalDateTime;

public class StringRepresetationsTest {

  private Person boris;

  @Before
  public void setUp() {
    boris = new Person("Boris", "Strugatzki", LocalDateTime.parse("1933-04-15"));
  }

  @Test
  public void stringRepresetationShouldNotEndWithNewline() {
    assertThat(StringRepresentations.stringRepresentationOf(boris)).doesNotEndWith("\n");
  }

  @Test
  public void shouldSerializeAndDeserialize() {
    assertThat(fromStringRepresetation(stringRepresentationOf(boris))).isEqualToComparingFieldByField(boris);
  }

}
