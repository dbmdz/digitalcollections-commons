package de.digitalcollections.commons.web;

import com.ibm.icu.text.Transliterator;
import java.util.Locale;

/**
 * Helper class to transform "special" characters into a slug compatible and human-readable form.
 */
public class SlugGenerator {

  private final Transliterator transliterator;

  /**
   * Default constructor for transliteration of german umlauts and further latin characters
   */
  public SlugGenerator() {
    this("Any-Latin; de-ASCII");
  }

  /**
   * Constructor, if other transliteration than german umlauts and further latin characters is required.
   * @param transliteratorId the ids for the transliteration charset(s)
   * @see <a href="https://unicode-org.github.io/icu-docs/apidoc/dev/icu4j/com/ibm/icu/text/Transliterator.html">https://unicode-org.github.io/icu-docs/apidoc/dev/icu4j/com/ibm/icu/text/Transliterator.html</a>
   */
  public SlugGenerator(String transliteratorId) {
    transliterator = Transliterator.getInstance(transliteratorId);
  }

  /**
   * Build a slug for a given string.
   *
   * <p>This is done by replacing all characters outside [A-Za-z0-9] by a dash, by avoiding
   * repeating dashes and dashes at the beginning and at the end. Additionally, special characters
   * like german umlauts are transformed into their base form. Last but not least, the calculated
   * slug is transformed to lowercase
   *
   * @param string as input
   * @return slug in a web-compatible but human readable form
   */
  public String generateSlug(String string) {
    if (string==null || string.isBlank()) {
      return string;
    }

    // Transform umlauts and others
    String slug = transliterator.transliterate(string);
    // All non A-Za-z0-9 characters are replaced by a dash
    slug = slug.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}-]", "-");
    // Repeating dashes are removed
    slug = slug.replaceAll("-+", "-");
    // Remove initial and final dashes
    slug = slug.replaceAll("^-+", "").replaceAll("-+$", "");
    // Transform string to lowercase
    slug = slug.toLowerCase(Locale.ROOT);
    return slug;
  }
}
