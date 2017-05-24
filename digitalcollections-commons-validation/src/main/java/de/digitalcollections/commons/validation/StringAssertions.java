package de.digitalcollections.commons.validation;

import java.util.Objects;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;

public class StringAssertions {

  public static final BiFunction<Integer, Integer, Boolean> AS_OFTEN_AS = Objects::equals;

  private String string;

  public StringAssertions(String value) {
    this.string = value;
  }

  public boolean isNull() {
    return string == null;
  }

  public boolean isEmpty() {
    return string == null || string.isEmpty();
  }

  public boolean contains(String value) {
    return this.string != null && this.string.contains(value);
  }

  public boolean doesNotContain(String value) {
    return !contains(value);
  }

  public boolean contains(String firstValue, BiFunction<Integer, Integer, Boolean> comparison, String secondValue) {
    return comparison.apply(
        StringUtils.countMatches(string, firstValue),
        StringUtils.countMatches(string, secondValue)
    );
  }

  public boolean doesNotContain(String firstValue, BiFunction<Integer, Integer, Boolean> comparison, String secondValue) {
    return !contains(firstValue, comparison, secondValue);
  }

}
