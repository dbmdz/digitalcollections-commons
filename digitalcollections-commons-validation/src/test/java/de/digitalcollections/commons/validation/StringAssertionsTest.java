package de.digitalcollections.commons.validation;

import java.util.Arrays;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class StringAssertionsTest {

  @Test
  public void isNullShouldBeTrueForNullValue() {
    assertThat(new StringAssertions(null).isNull()).isTrue();
  }

  @Test
  public void isNullShouldNotBeTrueForNonNullValue() {
    assertThat(new StringAssertions("").isNull()).isFalse();
  }

  @Test
  public void isEmptyShouldBeTrueForEmptyStrings() throws Exception {
    assertThat(Arrays.asList(null, "")).allMatch(s -> new StringAssertions(s).isEmpty());
  }

  @Test
  public void isEmptyShouldBeFalseForNonEmptyStrings() throws Exception {
    assertThat(new StringAssertions("abc").isEmpty()).isFalse();
  }

  @Test
  public void containsShouldBeTrueIfStringIsFound() throws Exception {
    assertThat(new StringAssertions("abc").contains("b")).isTrue();
  }

  @Test
  public void containsShouldBeFalseIfStringIsNotFound() throws Exception {
    assertThat(new StringAssertions("abc").contains("x")).isFalse();
  }

  @Test
  public void doesNotContainShouldBeTrueIfContainsIsFalse() throws Exception {
    StringAssertions assertion = new StringAssertions("abc");
    Assertions.assertThat(Arrays.asList("a", "x")).allMatch(s -> assertion.doesNotContain(s) != assertion.contains((String) s));
  }

  @Test
  public void containsAsOftenAsShouldBeTrue() throws Exception {
    StringAssertions assertion = new StringAssertions("abca");
    assertThat(assertion.contains("b", StringAssertions.AS_OFTEN_AS, "c")).isTrue();
  }

  @Test
  public void containsAsOftenAsShouldBeFalse() throws Exception {
    StringAssertions assertion = new StringAssertions("abca");
    assertThat(assertion.contains("a", StringAssertions.AS_OFTEN_AS, "c")).isFalse();
  }

  @Test
  public void doesNotContainAsOftenAsShouldBeFalseIfContainsIsTrue() throws Exception {
    StringAssertions assertion = new StringAssertions("abca");
    Assertions.assertThat(Arrays.asList("a", "c")).allMatch(
        s -> assertion.doesNotContain("b", StringAssertions.AS_OFTEN_AS, s) != assertion.contains("b", StringAssertions.AS_OFTEN_AS, s));
  }

}
