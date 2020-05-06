package de.digitalcollections.commons.xml.xpath;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The document reader")
class VariableResolverTest {

  @DisplayName("returns null for a locale with code null")
  @Test
  public void testNullLocale() {
    assertThat(VariableResolver.determineLocaleFromCode(null)).isNull();
  }

  @DisplayName("returns a locale with language and without script when only the language is given")
  @Test
  public void testLanguageOnlyLocale() {
    Locale loc = VariableResolver.determineLocaleFromCode("de");
    assertThat(loc.getLanguage()).isEqualTo("de");
    assertThat(loc.getScript()).isEmpty();
  }

  @DisplayName("handles unknown languages properly")
  @Test
  public void testUnknownLanguages() {
    Locale loc = VariableResolver.determineLocaleFromCode("und");
    assertThat(loc.getLanguage()).isEqualTo("und");
    assertThat(loc.getScript()).isEmpty();
  }

  @DisplayName("returns a locale with language and script for known locale codes")
  @Test
  public void testKnownLocaleCodes() {
    Locale loc = VariableResolver.determineLocaleFromCode("zh-Hani");
    assertThat(loc.getLanguage()).isEqualTo("zh");
    assertThat(loc.getScript()).isEqualTo("Hani");
  }

  @DisplayName("returns a locale with language and script for unknown locale codes")
  @Test
  public void testUnknownLocaleCodes() {
    Locale loc = VariableResolver.determineLocaleFromCode("und-Latn");
    assertThat(loc.getLanguage()).isEqualTo("und");
    assertThat(loc.getScript()).isEqualTo("Latn");
  }
}
