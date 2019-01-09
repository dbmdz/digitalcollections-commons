package de.digitalcollections.commons.yaml;

import de.digitalcollections.commons.yaml.examples.Person;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.digitalcollections.commons.yaml.StringRepresentations.fromStringRepresetation;
import static de.digitalcollections.commons.yaml.StringRepresentations.stringRepresentationOf;
import static org.assertj.core.api.Assertions.assertThat;

public class StringRepresentationsTest {

  private Person boris;

  @BeforeEach
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

  @Test
  public void stringRepresentationOfNullShould() {
    System.out.println(stringRepresentationOf(null));
  }

}
