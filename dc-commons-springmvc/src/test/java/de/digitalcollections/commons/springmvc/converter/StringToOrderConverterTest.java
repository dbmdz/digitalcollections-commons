package de.digitalcollections.commons.springmvc.converter;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.model.paging.Direction;
import de.digitalcollections.model.paging.NullHandling;
import de.digitalcollections.model.paging.Order;
import de.digitalcollections.model.paging.Sorting;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class StringToOrderConverterTest {

  StringToOrderConverter converter = new StringToOrderConverter();

  @Test
  public void testConversionNull() {
    Order converted = converter.convert(null);
    assertThat(converted).isNull();
  }

  @Test
  public void testConversionNotMatching() {
    String source = "created.nullfirst";
    Order converted = converter.convert(source);
    assertThat(converted).isNull();

    source = "created.DSC";
    converted = converter.convert(source);
    assertThat(converted).isNull();
  }

  @Test
  public void testConversionProperty() {
    String source = "created";
    Order converted = converter.convert(source);
    assertThat(converted.getProperty()).isEqualTo(source);
    assertThat(converted.getSubProperty()).isEqualTo(Optional.empty());
    assertThat(converted.getDirection()).isEqualTo(Sorting.DEFAULT_DIRECTION);
    assertThat(converted.getNullHandling()).isEqualTo(NullHandling.NATIVE);
  }

  @Test
  public void testConversionPropertySubProperty() {
    String source = "label_de";
    Order converted = converter.convert(source);
    assertThat(converted.getProperty()).isEqualTo("label");
    assertThat(converted.getSubProperty()).isEqualTo(Optional.of("de"));
    assertThat(converted.getDirection()).isEqualTo(Sorting.DEFAULT_DIRECTION);
    assertThat(converted.getNullHandling()).isEqualTo(NullHandling.NATIVE);
  }

  @Test
  public void testConversionPropertyDirection() {
    String source = "lastModified.desc";
    Order converted = converter.convert(source);
    assertThat(converted.getProperty()).isEqualTo("lastModified");
    assertThat(converted.getSubProperty()).isEqualTo(Optional.empty());
    assertThat(converted.getDirection()).isEqualTo(Direction.DESC);
    assertThat(converted.getNullHandling()).isEqualTo(NullHandling.NATIVE);
  }

  @Test
  public void testConversionPropertyNullHandling() {
    String source = "name.nullsfirst";
    Order converted = converter.convert(source);
    assertThat(converted.getProperty()).isEqualTo("name");
    assertThat(converted.getSubProperty()).isEqualTo(Optional.empty());
    assertThat(converted.getDirection()).isEqualTo(Sorting.DEFAULT_DIRECTION);
    assertThat(converted.getNullHandling()).isEqualTo(NullHandling.NULLS_FIRST);
  }

  @Test
  public void testFullConversion() {
    String source = "label_en.asc.nullslast";
    Order converted = converter.convert(source);
    assertThat(converted.getProperty()).isEqualTo("label");
    assertThat(converted.getSubProperty()).isEqualTo(Optional.of("en"));
    assertThat(converted.getDirection()).isEqualTo(Direction.ASC);
    assertThat(converted.getNullHandling()).isEqualTo(NullHandling.NULLS_LAST);
  }
}
