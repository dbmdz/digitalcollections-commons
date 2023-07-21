package de.digitalcollections.commons.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("The slug generator")
class SlugGeneratorTest {

  private SlugGenerator slugGenerator;

  @BeforeEach
  public void beforeEach() {
    slugGenerator = new SlugGenerator();
  }

  @DisplayName("returns null for a null input")
  @Test
  public void returnsNullForNull() {
    assertThat(slugGenerator.generateSlug(null)).isNull();
  }

  @DisplayName("returns an empty slug for an empty input")
  @Test
  public void returnsEmptyForEmpty() {
    assertThat(slugGenerator.generateSlug("")).isEqualTo("");
  }

  @DisplayName("transforms to lowercase")
  @Test
  public void transformToLowercase() {
    assertThat(slugGenerator.generateSlug("ABCDE")).isEqualTo("abcde");
  }

  @DisplayName("transforms umlauts to their standard form")
  @Test
  public void transformUmlautsToStandardForm() {
    assertThat(slugGenerator.generateSlug("äÄ")).isEqualTo("aeae");
  }

  @DisplayName("transforms all but digits, characters and dashes to dashes")
  @Test
  public void transformToDashes() {
    assertThat(slugGenerator.generateSlug("1/2 Kilo")).isEqualTo("1-2-kilo");
  }

  @DisplayName("strips dashes at the beginning and at the end")
  @Test
  public void noInitialAndFinalDashes() {
    assertThat(slugGenerator.generateSlug("(foo bar)")).isEqualTo("foo-bar");
  }

  @DisplayName("avoids repeating dashes")
  @Test
  public void noRepeatingDashes() {
    assertThat(slugGenerator.generateSlug("Paragraph $1")).isEqualTo("paragraph-1");
  }

  @DisplayName("does not trim, when the length is lower than the allowed length")
  @ParameterizedTest
  @CsvSource(value = {",", "12345 7890,12345-7890"})
  public void noTrimWhenShortEnough(String input, String expected) {
    slugGenerator.setMaxLength(10);
    assertThat(slugGenerator.generateSlug(input)).isEqualTo(expected);
  }

  @DisplayName("trims slugs without dashes exactly at maximum allowed length")
  @Test
  public void trimNoDashesAtMaximumLength() {
    slugGenerator.setMaxLength(10);
    assertThat(slugGenerator.generateSlug("1234567890123")).isEqualTo("1234567890");
  }

  @DisplayName("trims slugs with dashes at the last dash before reaching allowed length")
  @ParameterizedTest
  @CsvSource(
      value = {
        "123-567-90,123-567-90",
        "123-567-901,123-567",
        "123-5678901,123",
        "123-56-89-1,123-56-89",
        "123-56-8-012-45-78,123-56-8"
      })
  public void trimAtLastDashBeforeLimit(String input, String expected) {
    slugGenerator.setMaxLength(10);
    assertThat(slugGenerator.generateSlug(input)).isEqualTo(expected);
  }

  @DisplayName("does not trim on negative length limits")
  @ParameterizedTest
  @ValueSource(strings = {"123-567-90", "123-567-901", "123-5678901", "123-56-78-1"})
  public void noTrimmingOnNegativeLengthLimits(String input) {
    slugGenerator.setMaxLength(-10);
    assertThat(slugGenerator.generateSlug(input)).isEqualTo(input);
  }

  @DisplayName("validates valid slugs correctly")
  @ParameterizedTest
  @ValueSource(strings = {"", "abc-567-0", "101-foo-bar", "foo-bar-123"})
  public void isValid(String input) {
    assertThat(slugGenerator.isValidSlug(input)).isTrue();
  }

  @DisplayName("validates valid slugs correctly with case sensitivity")
  @ParameterizedTest
  @ValueSource(strings = {"abC-567-0", "101-FOO-bar", "foo-Bar-123"})
  public void isValidCaseSensitive(String input) {
    assertThat(slugGenerator.isValidSlugCaseSensitive(input)).isFalse();
  }

  @DisplayName("validates invalid slugs correctly")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "äbc-567-0",
        "u05d0%0aSet-Cookie:crlf=almaster/7a2f1935-c5b8-40fb-8622-c675de0a6242",
        "foo--bar",
        "-example-org-"
      })
  public void isInvalid(String input) {
    assertThat(slugGenerator.isValidSlug(input)).isFalse();
  }

  @DisplayName("validates invalid slugs correctly with max length")
  @ParameterizedTest
  @ValueSource(strings = {"äbc-567-0", "abc-567-0", "101-foo-bar", "foo-bar-123"})
  public void isInvalidMaxLength(String input) {
    slugGenerator.setMaxLength(5);
    assertThat(slugGenerator.isValidSlug(input)).isFalse();
  }
}
